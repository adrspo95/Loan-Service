package app.service.api.loan;

import app.dto.applicaion.LoanApplication;
import app.model.history.LoanEvent;
import app.model.loan.Loan;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * Created by GW95IB on 2017-05-26.
 */
public interface LoanService {

    List<Loan> findAll();

    Set<Loan> findAllForUserWithId(Long userId);

    Loan applyForLoan(Long userId, String userName, LoanApplication loanApplication, String clientIpAddress);

    Loan extendLoan(Long userId, Long loanId);
}
