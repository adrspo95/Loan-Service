package app.service.impl.account;

import app.model.account.Account;
import app.repository.account.AccountRepository;
import app.repository.user.UserRepository;
import app.service.api.account.AccountService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by GW95IB on 2017-05-26.
 */
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override public Account findAccountOfUserWithId(Long userId) {

        return userRepository.findOne(userId).getAccount();
    }

    @Override public Account updateAccountOfUserWithId(Account account, Long userId) {
        Account accountToBeUpdated = findAccountOfUserWithId(userId);

        accountToBeUpdated.setFirstName(account.getFirstName());
        accountToBeUpdated.setLastName(account.getLastName());
        accountToBeUpdated.setCreditCardNumber(account.getCreditCardNumber());

        return accountRepository.save(accountToBeUpdated);
    }
}
