package com.finwise.finwise.transfer;

import com.finwise.finwise.account.Account;
import com.finwise.finwise.account.AccountRepository;
import com.finwise.finwise.auth.User;
import com.finwise.finwise.auth.UserRepository;
import com.finwise.finwise.shared.exception.AccountNotFoundException;
import com.finwise.finwise.shared.exception.InsufficientBalanceException;
import com.finwise.finwise.shared.exception.InvalidCredentialsException;
import com.finwise.finwise.shared.exception.TransferNotFoundException;
import com.finwise.finwise.transfer.dto.TransferRequest;
import com.finwise.finwise.transfer.dto.TransferResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public TransferService(TransferRepository transferRepository, AccountRepository accountRepository,
            UserRepository userRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TransferResponse create(String email, TransferRequest request) {
        User user = resolveUser(email);

        if (request.fromAccountId().equals(request.toAccountId())) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }

        Account from = accountRepository.findByIdAndUserAndDeletedAtIsNull(request.fromAccountId(), user)
                .orElseThrow(AccountNotFoundException::new);

        Account to = accountRepository.findByIdAndUserAndDeletedAtIsNull(request.toAccountId(), user)
                .orElseThrow(AccountNotFoundException::new);

        validateSufficientBalance(from, request.amount());

        from.setBalance(from.getBalance().subtract(request.amount()));
        to.setBalance(to.getBalance().add(request.amount()));

        Transfer transfer = new Transfer();
        transfer.setAmount(request.amount());
        transfer.setDescription(request.description());
        transfer.setDate(request.date());
        transfer.setFromAccount(from);
        transfer.setToAccount(to);
        transfer.setUser(user);

        accountRepository.save(from);
        accountRepository.save(to);
        return TransferResponse.from(transferRepository.save(transfer));
    }

    @Transactional(readOnly = true)
    public List<TransferResponse> list(String email) {
        User user = resolveUser(email);
        return transferRepository.findByUserOrderByDateDesc(user).stream()
                .map(TransferResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransferResponse getById(String email, Long id) {
        User user = resolveUser(email);
        return TransferResponse.from(getOwnedTransfer(id, user));
    }

    @Transactional
    public void delete(String email, Long id) {
        User user = resolveUser(email);
        Transfer transfer = getOwnedTransfer(id, user);

        Account from = transfer.getFromAccount();
        Account to = transfer.getToAccount();

        from.setBalance(from.getBalance().add(transfer.getAmount()));
        to.setBalance(to.getBalance().subtract(transfer.getAmount()));

        transfer.setDeletedAt(Instant.now());

        accountRepository.save(from);
        accountRepository.save(to);
        transferRepository.save(transfer);
    }

    private User resolveUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
    }

    private Transfer getOwnedTransfer(Long id, User user) {
        return transferRepository.findByIdAndUser(id, user)
                .orElseThrow(TransferNotFoundException::new);
    }

    private void validateSufficientBalance(Account account, BigDecimal amount) {
        boolean blocksNegative = switch (account.getType()) {
            case CASH, CHECKING, SAVINGS -> true;
            case CREDIT_CARD -> false;
        };
        if (blocksNegative && account.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException();
        }
    }
}
