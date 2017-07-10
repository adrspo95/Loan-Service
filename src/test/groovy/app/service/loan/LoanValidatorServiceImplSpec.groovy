package app.service.loan

import app.dto.applicaion.LoanApplication
import app.exception.loan.LoanAlreadyExtendedException
import app.exception.loan.LoanNotExtendableException
import app.exception.loan.TooHighLoanAmountException
import app.exception.loan.TooLongLoanTermException
import app.exception.loan.TooLowLoanAmountException
import app.exception.loan.TooShortLoanTermException
import app.exception.user.UserInconsistencyException
import app.model.loan.Loan
import app.model.user.User
import app.repository.user.UserRepository
import app.service.api.loan.LoanChargeCalculatorService
import app.service.impl.loan.LoanValidatorServiceImpl
import spock.lang.Shared
import spock.lang.Specification


/**
 * Created by GW95IB on 2017-06-02.
 */
class LoanValidatorServiceImplSpec extends Specification {

    def static final USER_ID = 1
    def static final USERNAME = "user"

    def static final MAX_LOAN_AMOUNT = 10000.0
    def static final MIN_LOAN_AMOUNT = 100.0
    def static final MAX_TERM_IN_DAYS = 30
    def static final MIN_TERM_IN_DAYS = 1

    def loanChargeCalculatorService = Mock(LoanChargeCalculatorService)

    def loanValidatorService = new LoanValidatorServiceImpl(MAX_LOAN_AMOUNT, MIN_LOAN_AMOUNT, MAX_TERM_IN_DAYS,
            MIN_TERM_IN_DAYS, loanChargeCalculatorService, userRepository)

    @Shared
    def userRepository = Mock(UserRepository)
    @Shared
    def user = new User(id: USER_ID, username: USERNAME)

    def setupSpec() {
        userRepository.findOne(USER_ID) >> user
    }

    def "loan application with proper amount and term should pass validation"(double amount, int termInDays) {

        given:
        def loanApplication = new LoanApplication(amount: 100, termInDays: termInDays)

        when:
        loanValidatorService.validateLoanApplication(loanApplication, USER_ID, USERNAME)

        then:
        noExceptionThrown()

        where:
        amount                              | termInDays
        MAX_LOAN_AMOUNT                     | MAX_TERM_IN_DAYS
        (MAX_LOAN_AMOUNT - MIN_LOAN_AMOUNT) | (MAX_TERM_IN_DAYS - MIN_TERM_IN_DAYS)
        MIN_LOAN_AMOUNT                     | MIN_TERM_IN_DAYS
    }

    def "loan application with too low amount should not pass validation"() {

        given:
        def loanApplication = new LoanApplication(amount: MIN_LOAN_AMOUNT - 1, termInDays: MIN_TERM_IN_DAYS)

        when:
        loanValidatorService.validateLoanApplication(loanApplication, USER_ID, USERNAME)

        then:
        thrown TooLowLoanAmountException
    }

    def "loan application with too high amount should not pass validation"() {

        given:
        def loanApplication = new LoanApplication(amount: MAX_LOAN_AMOUNT + 1, termInDays: MIN_TERM_IN_DAYS)

        when:
        loanValidatorService.validateLoanApplication(loanApplication, USER_ID, USERNAME)

        then:
        thrown TooHighLoanAmountException
    }

    def "loan application with too short term should not pass validation"() {

        given:
        def loanApplication = new LoanApplication(amount: MIN_TERM_IN_DAYS, termInDays: MIN_TERM_IN_DAYS - 1)

        when:
        loanValidatorService.validateLoanApplication(loanApplication, USER_ID, USERNAME)

        then:
        thrown TooShortLoanTermException
    }

    def "loan application with too long term should not pass validation"() {

        given:
        def loanApplication = new LoanApplication(amount: MIN_TERM_IN_DAYS, termInDays: MAX_TERM_IN_DAYS + 1)

        when:
        loanValidatorService.validateLoanApplication(loanApplication, USER_ID, USERNAME)

        then:
        thrown TooLongLoanTermException
    }

    def "loan application for inconsistent user should not pass validation"() {

        given:
        def loanApplication = new LoanApplication(amount: MIN_TERM_IN_DAYS, termInDays: MAX_TERM_IN_DAYS)

        def final INCONSISTENT_USERNAME = "inconsistent_username"

        when:
        loanValidatorService.validateLoanApplication(loanApplication, USER_ID, INCONSISTENT_USERNAME)

        then:
        thrown UserInconsistencyException
    }

    def "not yet extended loan with amount not exceeding max amount after extending should be extensible"() {

        given:
        def loan = new Loan(amount: MIN_LOAN_AMOUNT, extended: false)

        loanChargeCalculatorService.calculateExtensionCharge(loan.amount) >> MIN_LOAN_AMOUNT + 100

        when:
        loanValidatorService.validateLoanForExtension(loan)

        then:
        noExceptionThrown()
    }

    def "exception should be thrown if loan cannot be extended due to too high amount"() {

        given:
        def loan = new Loan(amount: MAX_LOAN_AMOUNT, extended: false)

        loanChargeCalculatorService.calculateExtensionCharge(loan.amount) >> MAX_LOAN_AMOUNT + 100

        when:
        loanValidatorService.validateLoanForExtension(loan)

        then:
        thrown LoanNotExtendableException
    }

    def "exception should be thrown if loan was already extended"() {

        given:
        def loan = new Loan(amount: MIN_LOAN_AMOUNT, extended: true)

        loanChargeCalculatorService.calculateExtensionCharge(loan.amount) >> MIN_LOAN_AMOUNT + 100

        when:
        loanValidatorService.validateLoanForExtension(loan)

        then:
        thrown LoanAlreadyExtendedException
    }

}
