package app.controller.loan;

import static app.controller.account.AccountController.USER_ACCOUNT;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import app.configuration.security.authorization.AuthorizationRule;
import app.repository.user.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import app.dto.applicaion.LoanApplication;
import app.model.loan.Loan;
import app.service.api.loan.LoanService;

/**
 * Created by GW95IB on 2017-05-25.
 */
@RestController
public class LoanController {

    private static final String LOANS = "/loans";
    private static final String USER_ACCOUNT_LOANS = USER_ACCOUNT + LOANS;
    private static final String USER_ACCOUNT_LOAN = USER_ACCOUNT + LOANS + "/{loanId}";

    private final LoanService loanService;
    private final UserRepository userRepository;

    public LoanController(LoanService loanService, UserRepository userRepository) {
        this.loanService = loanService;
        this.userRepository = userRepository;
    }

    @GetMapping(LOANS)
    @PreAuthorize(AuthorizationRule.IS_ADMIN)
    public List<Loan> findAll() {
        return loanService.findAll();
    }

    @GetMapping(USER_ACCOUNT_LOANS)
    @PreAuthorize(AuthorizationRule.IS_ADMIN_OR_USER_WITH_SPECIFIED_ID)
    public Set<Loan> findAllUserLoans(@PathVariable Long userId) {
        return loanService.findAllForUserWithId(userId);
    }

    @PostMapping(USER_ACCOUNT_LOANS)
    @PreAuthorize(AuthorizationRule.IS_USER_WITH_SPECIFIED_ID)
    public Loan requestForLoan(
            @PathVariable Long userId,
            @RequestBody LoanApplication loanApplication,
            HttpServletRequest httpRequest) {
        return loanService.applyForLoan(userId, httpRequest.getUserPrincipal().getName(), loanApplication,
                httpRequest.getRemoteAddr());
    }

    @PutMapping(USER_ACCOUNT_LOAN)
    @PreAuthorize(AuthorizationRule.IS_USER_WITH_SPECIFIED_ID)
    public Loan extendLoan(@PathVariable Long userId, @PathVariable Long loanId) {
        return loanService.extendLoan(userId, loanId);
    }

}
