package app.service.impl.loan;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import app.dto.applicaion.LoanApplication;
import app.model.loan.Loan;
import app.repository.history.LoanEventRepository;
import app.repository.loan.LoanRepository;
import app.repository.user.UserRepository;
import app.service.api.history.LoanEventLoggerService;
import app.service.api.loan.LoanChargeCalculatorService;
import app.service.api.loan.LoanService;
import app.service.api.loan.LoanValidatorService;
import app.service.api.loan.RiskAnalysisService;

/**
 * Created by GW95IB on 2017-05-26.
 */
@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final RiskAnalysisService riskAnalysisService;
    private final LoanEventLoggerService loanEventLoggerService;
    private final LoanChargeCalculatorService loanChargeCalculatorService;
    private final LoanValidatorService loanValidatorService;

    public LoanServiceImpl(LoanRepository loanRepository, UserRepository userRepository, LoanEventRepository loanEventRepository, RiskAnalysisService riskAnalysisService, LoanEventLoggerService loanEventLoggerService, LoanChargeCalculatorService loanChargeCalculatorService, LoanValidatorService loanValidatorService) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.riskAnalysisService = riskAnalysisService;
        this.loanEventLoggerService = loanEventLoggerService;
        this.loanChargeCalculatorService = loanChargeCalculatorService;
        this.loanValidatorService = loanValidatorService;
    }

    @Override
    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    @Override
    public Set<Loan> findAllForUserWithId(Long userId) {
        return userRepository.findOne(userId).getAccount().getLoans();
    }

    @Override
    public Loan applyForLoan(
            Long userId,
            String userName,
            LoanApplication loanApplication,
            String clientIpAddress) {
        loanValidatorService.validateLoanApplication(loanApplication, userId, userName);

        loanEventLoggerService.logApplication(loanApplication, clientIpAddress, userName);

        riskAnalysisService.performRiskAnalysisFor(loanApplication, clientIpAddress, userName);

        LocalDate currentDate = LocalDate.now();

        Loan loan = Loan.builder()
                .amount(loanApplication.getAmount())
                .termInDays(loanApplication.getTermInDays())
                .extended(false)
                .issueDate(currentDate)
                .paymentDeadlineDate(currentDate.plusDays(loanApplication.getTermInDays()))
                .charge(loanChargeCalculatorService.calculateChargeFor(loanApplication))
                .build();

        loanRepository.save(loan);

        loanEventLoggerService.logIssuance(loan, clientIpAddress, userName);

        return loan;
    }

    @Override
    public Loan extendLoan(Long userId, Long loanId) {
        Loan loan = loanRepository.getOne(loanId);
        Double extendedCharge = loanChargeCalculatorService.calculateExtensionCharge(loan.getCharge());

        loanValidatorService.validateLoanForExtension(loan);

        loan = loan.toBuilder()
                .extended(true)
                .charge(extendedCharge)
                .paymentDeadlineDate(loan.getPaymentDeadlineDate().plusWeeks(1))
                .build();

        return loanRepository.save(loan);
    }
}
