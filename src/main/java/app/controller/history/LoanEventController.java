package app.controller.history;

import static app.controller.account.AccountController.USER_ACCOUNT;

import java.util.List;
import java.util.Set;

import app.configuration.security.authorization.AuthorizationRule;
import app.repository.user.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import app.model.history.LoanEvent;
import app.service.api.history.LoanEventService;

/**
 * Created by GW95IB on 2017-05-25.
 */
@RestController
public class LoanEventController {

    private static final String LOAN_EVENTS = "/history";
    private static final String USER_LOAN_EVENTS = USER_ACCOUNT + LOAN_EVENTS;

    private final LoanEventService loanEventService;
    private final UserRepository userRepository;

    public LoanEventController(LoanEventService loanEventService, UserRepository userRepository) {
        this.loanEventService = loanEventService;
        this.userRepository = userRepository;
    }

    @GetMapping(LOAN_EVENTS)
    @PreAuthorize(AuthorizationRule.IS_ADMIN)
    public List<LoanEvent> findAll() {
        return loanEventService.findAll();
    }

    @GetMapping(USER_LOAN_EVENTS)
    @PreAuthorize(AuthorizationRule.IS_ADMIN_OR_USER_WITH_SPECIFIED_ID)
    public Set<LoanEvent> findAllForUserWithId(@PathVariable Long userId) {
        return loanEventService.findAllForUserWithId(userId);
    }
}
