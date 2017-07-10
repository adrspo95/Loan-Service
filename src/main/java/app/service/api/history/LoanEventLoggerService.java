package app.service.api.history;

import app.dto.applicaion.LoanApplication;
import app.model.loan.Loan;

/**
 * Created by GW95IB on 2017-05-26.
 */
public interface LoanEventLoggerService {
    void logApplication(LoanApplication loanApplication, String clientIpAddress, String userName);

    void logRejection(LoanApplication loanApplication, String clientIpAddress, String userName);

    void logIssuance(Loan loan, String clientIpAddress, String userName);

    void logExtension(Loan loan, String clientIpAddress, String userName);

}
