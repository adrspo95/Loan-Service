package app.service.impl.loan;

import app.dto.applicaion.LoanApplication;
import app.exception.loan.LoanAlreadyExtendedException;
import app.exception.loan.LoanNotExtendableException;
import app.exception.loan.TooHighLoanAmountException;
import app.exception.loan.TooLongLoanTermException;
import app.exception.loan.TooLowLoanAmountException;
import app.exception.loan.TooShortLoanTermException;
import app.exception.user.UserInconsistencyException;
import app.model.loan.Loan;
import app.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import app.service.api.loan.LoanChargeCalculatorService;
import app.service.api.loan.LoanValidatorService;

/**
 * Created by GW95IB on 2017-06-02.
 */
@Service
public class LoanValidatorServiceImpl implements LoanValidatorService {

    private final Double maxLoanAmount;
    private final Double minLoanAmount;
    private final Integer maxTermInDays;
    private final Integer minTermInDays;

    private final LoanChargeCalculatorService loanChargeCalculatorService;
    private final UserRepository userRepository;

    public LoanValidatorServiceImpl(@Value("${loan.max.amount}") Double maxLoanAmount, @Value("${loan.min.amount}") Double minLoanAmount, @Value("${loan.max.term}") Integer maxTermInDays, @Value("${loan.min.term}") Integer minTermInDays, LoanChargeCalculatorService loanChargeCalculatorService, UserRepository userRepository) {
        this.maxLoanAmount = maxLoanAmount;
        this.minLoanAmount = minLoanAmount;
        this.maxTermInDays = maxTermInDays;
        this.minTermInDays = minTermInDays;
        this.loanChargeCalculatorService = loanChargeCalculatorService;
        this.userRepository = userRepository;
    }

    @Override
    public void validateLoanApplication(LoanApplication loanApplication, Long userId, String userName) {
        validateUserConsistency(userId, userName);
        validateTerm(loanApplication.getTermInDays());
        validateAmount(loanApplication.getAmount());
    }

    @Override public void validateLoanForExtension(Loan loan) {
        if (loan.getExtended()) {
            throw new LoanAlreadyExtendedException(loan.getId());
        }

        if (loanChargeCalculatorService.calculateExtensionCharge(loan.getAmount()) > maxLoanAmount) {
            throw new LoanNotExtendableException(loan.getId());
        }
    }

    private void validateTerm(Integer termInDays) {
        if (termInDays < minTermInDays) {
            throw new TooShortLoanTermException(minTermInDays);
        }
        if (termInDays > maxTermInDays) {
            throw new TooLongLoanTermException(maxTermInDays);
        }
    }

    private void validateAmount(Double amount) {
        if (amount < minLoanAmount) {
            throw new TooLowLoanAmountException(minLoanAmount);
        }
        if (amount > maxLoanAmount) {
            throw new TooHighLoanAmountException(maxLoanAmount);
        }
    }

    private void validateUserConsistency(Long userId, String userName) {
        String foundUserName = userRepository.findOne(userId).getUsername();

        if (!foundUserName.equals(userName)) {
            throw new UserInconsistencyException(userId);
        }
    }
}
