package app.service.impl.loan;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import app.dto.applicaion.LoanApplication;
import app.service.api.loan.LoanChargeCalculatorService;

/**
 * Created by GW95IB on 2017-06-02.
 */
@Service
public class LoanChargeCalculatorServiceImpl implements LoanChargeCalculatorService {

    private final Double commissionRate;
    private final Double interestRate;
    private final Double interestsExtendFactor;

    public LoanChargeCalculatorServiceImpl(@Value("${loan.commission.rate}") Double commissionRate, @Value("${loan.daily.interest.rate}") Double interestRate, @Value("${loan.interests.extend.factor}") Double interestsExtendFactor) {
        this.commissionRate = commissionRate;
        this.interestRate = interestRate;
        this.interestsExtendFactor = interestsExtendFactor;
    }

    @Override
    public Double calculateChargeFor(LoanApplication loanApplication) {
        double amount = loanApplication.getAmount();

        double commission = amount * commissionRate;
        double interest = amount * interestRate * loanApplication.getTermInDays();

        return commission + interest;
    }

    @Override
    public Double calculateExtensionCharge(Double charge) {
        return charge * interestsExtendFactor;
    }
}
