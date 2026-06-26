package com.finwise.finwise.account;

import com.finwise.finwise.account.dto.AccountRequest;
import com.finwise.finwise.account.dto.AccountResponse;
import com.finwise.finwise.shared.exception.AccountNotFoundException;
import com.finwise.finwise.auth.User;
import com.finwise.finwise.auth.UserRepository;
import com.finwise.finwise.shared.exception.InvalidCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {
    private final AccountRepository repo;
    private final UserRepository userRepo;

    public AccountService(AccountRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    @Transactional
    public AccountResponse create(String email, AccountRequest request) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        Account account = new Account();
        account.setName(request.name());
        account.setType(request.type());
        account.setBalance(request.balance());
        account.setUser(user);

        Account saved = repo.save(account);
        return AccountResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> listByUser(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        return repo.findByUser(user).stream()
                .map(AccountResponse::from)
                .toList();
    }

    private Account getOwnedAccount(String email, Long accountId) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        return repo.findByIdAndUser(accountId, user)
                .orElseThrow(AccountNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public AccountResponse getById(String email, Long accountId){
        Account account = getOwnedAccount(email, accountId);
        return AccountResponse.from(account);
    }

    @Transactional
    public AccountResponse update(String email, Long accountId, AccountRequest request){
        Account account = getOwnedAccount(email, accountId);

        account.setName(request.name());
        account.setType(request.type());
        account.setBalance(request.balance());

        Account saved = repo.save(account);
        return AccountResponse.from(saved);
    }

    @Transactional
    public void delete(String email, Long accountId){
        Account account = getOwnedAccount(email, accountId);
        repo.delete(account);
    }
}
