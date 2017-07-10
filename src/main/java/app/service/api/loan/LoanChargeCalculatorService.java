package app.service.api.loan;

import app.dto.applicaion.LoanApplication;

/**
 * Created by GW95IB on 2017-06-02.
 */
public interface LoanChargeCalculatorService {
    Double calculateChargeFor(LoanApplication loanApplication);
    Double calculateExtensionCharge(Double charge);
}
