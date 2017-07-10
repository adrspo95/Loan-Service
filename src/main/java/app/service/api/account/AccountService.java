package app.service.api.account;

import app.model.account.Account;

import java.util.List;

/**
 * Created by GW95IB on 2017-05-26.
 */
public interface AccountService {
    List<Account> findAll();

    Account findAccountOfUserWithId(Long userId);

    Account updateAccountOfUserWithId(Account account, Long userId);
}
