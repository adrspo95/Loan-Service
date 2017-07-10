package app.service.api.loan;

import app.dto.applicaion.LoanApplication;

/**
 * Created by GW95IB on 2017-05-26.
 */
public interface RiskAnalysisService {

    void performRiskAnalysisFor(LoanApplication loanApplication, String clientIpAddress, String userName);
}
