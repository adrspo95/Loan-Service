package app.service.loan

import app.dto.applicaion.LoanApplication
import app.model.history.LoanEventType
import app.repository.history.LoanEventRepository
import app.service.api.history.LoanEventLoggerService
import app.service.impl.loan.RiskAnalysisServiceImpl
import spock.lang.Specification

import java.time.LocalDateTime

/**
 * Created by GW95IB on 2017-05-29.
 */
class RiskAnalysisServiceImplSpec extends Specification {

    static final def MAX_LOAN_AMOUNT = 10000.0d
    static final def MAX_LOANS_FROM_SINGLE_IP_PER_DAY = 3

    static final def USERNAME = "user"
    static final def IP_ADDRESS = "127.0.0.1"

    def loanEventRepository = Mock(LoanEventRepository)
    def loanEventLoggerService = Mock(LoanEventLoggerService)

    def riskAnalysisService = new RiskAnalysisServiceImpl(MAX_LOAN_AMOUNT, MAX_LOANS_FROM_SINGLE_IP_PER_DAY, loanEventRepository, loanEventLoggerService)

    def "proper loan application should pass validation"() {

        given:
        def loanApplication = new LoanApplication(amount: 6000.0d, termInDays: 14)

        loanEventRepository
                .countByTypeAndClientIpAddressAndDateAfter(LoanEventType.ISSUED, IP_ADDRESS, _ as LocalDateTime) >> 0

        when:
        riskAnalysisService.performRiskAnalysisFor(loanApplication, IP_ADDRESS, USERNAME)

        then:
        noExceptionThrown()
    }


}
