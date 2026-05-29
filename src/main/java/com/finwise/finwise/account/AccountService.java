package com.finwise.finwise.account;

import com.finwise.finwise.account.dto.AccountRequest;
import com.finwise.finwise.account.dto.AccountResponse;
import com.finwise.finwise.auth.User;
import com.finwise.finwise.auth.UserRepository;
import com.finwise.finwise.shared.exception.InvalidCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    private final AccountRepository repo;
    private final UserRepository userRepo;

    public AccountService(AccountRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    public AccountResponse create(String email, AccountRequest request){
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

    public List<AccountResponse> listByUser(String email){
        User user = userRepo.findByEmail(email)
            .orElseThrow(InvalidCredentialsException::new);
        
        return repo.findByUser(user).stream()
            .map(AccountResponse::from)
            .toList();
    }
}
