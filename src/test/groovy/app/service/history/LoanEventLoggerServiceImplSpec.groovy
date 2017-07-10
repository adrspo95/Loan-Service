package app.service.history

import app.dto.applicaion.LoanApplication
import app.model.history.LoanEvent
import app.model.history.LoanEventType
import app.model.loan.Loan
import app.repository.history.LoanEventRepository
import app.service.impl.history.LoanEventLoggerServiceServiceImpl
import spock.lang.Specification

import java.time.LocalDateTime

/**
 * Created by GW95IB on 2017-05-29.
 */
class LoanEventLoggerServiceImplSpec extends Specification {

    static final def CLIENT_IP_ADDRESS = "127.0.0.1"
    static final def USERNAME = "local"
    static final def LOAN_APPLICATION = new LoanApplication(amount: 1000.0, termInDays: 10)
    static final def LOAN = new Loan(id: 1, amount: 1000.0, termInDays: 10)

    def loanEventRepository = Mock(LoanEventRepository)

    def loanEventLoggerService = new LoanEventLoggerServiceServiceImpl(loanEventRepository)

    LoanEvent loanEvent

    def "should log application event"() {

        when:
        loanEventLoggerService.logApplication(LOAN_APPLICATION, CLIENT_IP_ADDRESS, USERNAME)

        then:
        1 * loanEventRepository.save(_) >> { arguments -> loanEvent = arguments[0] }

        loanEvent.type == LoanEventType.APPLIED
        loanEvent.loanId == null

        assertProperStateOfLoanEvent()
    }

    def "should log rejection event"() {

        when:
        loanEventLoggerService.logRejection(LOAN_APPLICATION, CLIENT_IP_ADDRESS, USERNAME)

        then:
        1 * loanEventRepository.save(_) >> { arguments -> loanEvent = arguments[0] }

        loanEvent.type == LoanEventType.REJECTED
        loanEvent.loanId == null

        assertProperStateOfLoanEvent()
    }

    def "should log issuance event"() {

        when:
        loanEventLoggerService.logIssuance(LOAN, CLIENT_IP_ADDRESS, USERNAME)

        then:
        1 * loanEventRepository.save(_) >> { arguments -> loanEvent = arguments[0] }

        loanEvent.type == LoanEventType.ISSUED
        loanEvent.loanId == LOAN.id

        assertProperStateOfLoanEvent()
    }

    def "should log extension event"() {

        when:
        loanEventLoggerService.logExtension(LOAN, CLIENT_IP_ADDRESS, USERNAME)

        then:
        1 * loanEventRepository.save(_) >> { arguments -> loanEvent = arguments[0] }

        loanEvent.type == LoanEventType.EXTENDED
        loanEvent.loanId == LOAN.id

        assertProperStateOfLoanEvent()
    }

    private void assertProperStateOfLoanEvent() {
        def date = loanEvent.date
        def now = LocalDateTime.now()

        assert loanEvent.amount == LOAN_APPLICATION.amount
        assert loanEvent.termInDays == LOAN_APPLICATION.termInDays
        assert loanEvent.clientIpAddress == CLIENT_IP_ADDRESS
        assert loanEvent.userName == USERNAME
        assert date.minusSeconds(5).isBefore(now) && date.plusSeconds(5).isAfter(now)
    }
}
