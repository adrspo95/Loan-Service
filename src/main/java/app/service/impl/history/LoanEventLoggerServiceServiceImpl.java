package app.service.impl.history;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import app.dto.applicaion.LoanApplication;
import app.model.history.LoanEvent;
import app.model.history.LoanEventType;
import app.model.loan.Loan;
import app.repository.history.LoanEventRepository;
import app.service.api.history.LoanEventLoggerService;

/**
 * Created by GW95IB on 2017-05-26.
 */
@Service
public class LoanEventLoggerServiceServiceImpl implements LoanEventLoggerService {
    private final LoanEventRepository loanEventRepository;

    public LoanEventLoggerServiceServiceImpl(LoanEventRepository loanEventRepository) {
        this.loanEventRepository = loanEventRepository;
    }

    @Override
    public void logApplication(LoanApplication loanApplication, String clientIpAddress, String userName) {
        LoanEvent loanEvent = getLogEventBuilderBaseOn(loanApplication, clientIpAddress, userName)
                .type(LoanEventType.APPLIED).build();

        create(loanEvent);
    }

    @Override
    public void logRejection(LoanApplication loanApplication, String clientIpAddress, String userName) {
        LoanEvent loanEvent = getLogEventBuilderBaseOn(loanApplication, clientIpAddress, userName)
                .type(LoanEventType.REJECTED).build();

        create(loanEvent);
    }

    @Override
    public void logIssuance(Loan loan, String clientIpAddress, String userName) {
        LoanEvent loanEvent = getLogEventBuilderBaseOn(loan, clientIpAddress, userName)
                .type(LoanEventType.ISSUED).build();

        create(loanEvent);
    }

    @Override
    public void logExtension(Loan loan, String clientIpAddress, String userName) {
        LoanEvent loanEvent = getLogEventBuilderBaseOn(loan, clientIpAddress, userName)
                .type(LoanEventType.EXTENDED).build();

        create(loanEvent);
    }

    private LoanEvent.LoanEventBuilder getLogEventBuilderBaseOn(
            LoanApplication loanApplication,
            String clientIpAddress,
            String userName) {
        return LoanEvent.builder()
                .amount(loanApplication.getAmount())
                .termInDays(loanApplication.getTermInDays())
                .userName(userName)
                .clientIpAddress(clientIpAddress)
                .date(LocalDateTime.now());
    }

    private LoanEvent.LoanEventBuilder getLogEventBuilderBaseOn(Loan loan, String clientIpAddress, String userName) {
        return LoanEvent.builder()
                .loanId(loan.getId())
                .amount(loan.getAmount())
                .termInDays(loan.getTermInDays())
                .userName(userName)
                .clientIpAddress(clientIpAddress)
                .date(LocalDateTime.now());
    }

    private void create(LoanEvent loanEvent) {
        loanEventRepository.save(loanEvent);
    }
}
