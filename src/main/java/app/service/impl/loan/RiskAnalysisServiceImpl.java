package app.service.impl.loan;

import java.time.LocalDateTime;
import java.time.LocalTime;

import app.service.api.history.LoanEventLoggerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import app.dto.applicaion.LoanApplication;
import app.exception.loan.LoanApplicationRejectedException;
import app.model.history.LoanEventType;
import app.repository.history.LoanEventRepository;
import app.service.api.loan.RiskAnalysisService;

/**
 * Created by GW95IB on 2017-05-26.
 */
@Service
public class RiskAnalysisServiceImpl implements RiskAnalysisService {

    private static final LocalTime MIDNIGHT = LocalTime.MIDNIGHT;
    private static final LocalTime SIX_MORNING = LocalTime.MIDNIGHT.plusHours(6);

    private final Double maxLoanAmount;
    private final Integer maxLoansFromSingleIpPerDay;

    private final LoanEventRepository loanEventRepository;
    private final LoanEventLoggerService loanEventLoggerService;

    public RiskAnalysisServiceImpl(@Value("${loan.max.amount}") Double maxLoanAmount, @Value("${loan.max.loans.from.single.ip.per.day}") Integer maxLoansFromSingleIpPerDay, LoanEventRepository loanEventRepository, LoanEventLoggerService loanEventLoggerService) {
        this.maxLoanAmount = maxLoanAmount;
        this.maxLoansFromSingleIpPerDay = maxLoansFromSingleIpPerDay;
        this.loanEventRepository = loanEventRepository;
        this.loanEventLoggerService = loanEventLoggerService;
    }

    @Override
    public void performRiskAnalysisFor(
            LoanApplication loanApplication,
            String clientIpAddress,
            String userName) {
        checkTimeAndAmount(loanApplication, clientIpAddress, userName);
        checkForMultipleRequestsFromSingleIp(loanApplication, clientIpAddress, userName);
    }

    private void checkTimeAndAmount(LoanApplication loanApplication,  String clientIpAddress, String userName) {
        LocalTime currentTime = LocalTime.now();

        if (currentTime.isAfter(MIDNIGHT) && currentTime.isBefore(SIX_MORNING) && (loanApplication.getAmount() == maxLoanAmount)) {
            rejectLoanApplication(loanApplication, clientIpAddress, userName);
        }
    }

    private void checkForMultipleRequestsFromSingleIp(LoanApplication loanApplication, String clientIpAddress, String userName) {
        LocalDateTime oneDayBefore = LocalDateTime.now().minusHours(24);

        Integer issuedLoansWithinDay = loanEventRepository
                .countByTypeAndClientIpAddressAndDateAfter(LoanEventType.ISSUED, clientIpAddress, oneDayBefore);

        if(issuedLoansWithinDay > maxLoansFromSingleIpPerDay) {
            rejectLoanApplication(loanApplication, clientIpAddress, userName);
        }
    }

    private void rejectLoanApplication(LoanApplication loanApplication, String clientIpAddress, String userName) {
        loanEventLoggerService.logRejection(loanApplication, clientIpAddress, userName);

        throw new LoanApplicationRejectedException();
    }

}
