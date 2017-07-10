package app.service.api.loan;

import app.dto.applicaion.LoanApplication;
import app.model.loan.Loan;

/**
 * Created by GW95IB on 2017-06-02.
 */
public interface LoanValidatorService {
    void validateLoanApplication(LoanApplication loanApplication, Long userId, String userName);

    void validateLoanForExtension(Loan loan);
}
