package app.controller.account;

import java.security.Principal;
import java.util.List;

import app.configuration.security.authorization.AuthorizationRule;
import app.repository.user.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import app.controller.user.UserController;
import app.model.account.Account;
import app.service.api.account.AccountService;

/**
 * Created by GW95IB on 2017-05-25.
 */
@RestController
public class AccountController {

    private static final String ACCOUNTS = "/accounts";

    public static final String USER_ACCOUNT = UserController.USER + "/account";

    private final AccountService accountService;
    private final UserRepository userRepository;

    public AccountController(AccountService accountService, UserRepository userRepository) {
        this.accountService = accountService;
        this.userRepository = userRepository;
    }

    @GetMapping(ACCOUNTS)
    @PreAuthorize(AuthorizationRule.IS_ADMIN)
    public List<Account> findAll() {
        return accountService.findAll();
    }

    @GetMapping(USER_ACCOUNT)
    @PreAuthorize(AuthorizationRule.IS_ADMIN_OR_USER_WITH_SPECIFIED_ID)
    public Account findAccountOfUserWithId(@PathVariable Long userId) {
        return accountService.findAccountOfUserWithId(userId);
    }

    @PutMapping(USER_ACCOUNT)
    @PreAuthorize(AuthorizationRule.IS_USER_WITH_SPECIFIED_ID)
    public Account updateAccountOfUserWithId(@RequestBody Account account, @PathVariable Long userId, Principal principial) {
        Account uaccount = accountService.updateAccountOfUserWithId(account, userId);
        return uaccount;
    }
}
