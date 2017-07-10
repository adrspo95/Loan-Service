package app.service.loan

import app.dto.applicaion.LoanApplication
import app.model.account.Account
import app.model.loan.Loan
import app.model.user.User
import app.repository.history.LoanEventRepository
import app.repository.loan.LoanRepository
import app.repository.user.UserRepository
import app.service.api.history.LoanEventLoggerService
import app.service.api.loan.RiskAnalysisService
import app.service.impl.loan.LoanChargeCalculatorServiceImpl
import app.service.impl.loan.LoanServiceImpl
import app.service.impl.loan.LoanValidatorServiceImpl
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate

/**
 * Created by GW95IB on 2017-05-29.
 */
class LoanServiceImplSpec extends Specification {

    def static final USERNAME = "user@user.com"
    def static final USER_ID = 1
    def static final CLIENT_IP_ADDRESS = "127.0.0.1"
    def static final CALCULATED_CHARGE = 500.0
    def static final EXTENDED_CHARGE = 1500.0

    def loanRepository = Mock(LoanRepository)
    def userRepository = Mock(UserRepository)
    def loanEventRepository = Mock(LoanEventRepository)
    def loanEventLogger = Mock(LoanEventLoggerService)
    def riskAnalysisService = Mock(RiskAnalysisService)
    def loanChargeCalculatorService = Mock(LoanChargeCalculatorServiceImpl)
    def loanValidatorService = Mock(LoanValidatorServiceImpl)


    def loanService = new LoanServiceImpl(loanRepository, userRepository, loanEventRepository, riskAnalysisService,
            loanEventLogger, loanChargeCalculatorService, loanValidatorService)

    @Shared
    List<Loan> loans
    @Shared
    Loan loan1, loanToExtend
    @Shared
    User user
    @Shared
    LoanApplication loanApplication

    Loan expectedLoan

    def setup() {
        loan1 = new Loan(id: 1, amount: 1000, paymentDeadlineDate: LocalDate.now())
        loanToExtend = new Loan(id: 2, amount: 2000, paymentDeadlineDate: LocalDate.now(), extended: false, charge: 1000)

        loans = [loan1, loanToExtend]

        loanApplication = new LoanApplication(amount: 1000, termInDays: 10)

        def account = new Account(emailAddress: USERNAME, firstName: "Foo", lastName: "Bar", loans: loans)

        user = new User(username: USERNAME, id: USER_ID, account: account)
    }

    def "should return all loans"() {

        given:
        loanRepository.findAll() >> [loan1, loanToExtend]

        when:
        List<Loan> foundLoans = loanService.findAll()

        then:
        foundLoans == loans
    }

    def "should return User's Loans"() {

        given:
        userRepository.findOne(USER_ID) >> user

        when:
        Set<Loan> foundLoans = loanService.findAllForUserWithId(USER_ID)

        then:
        foundLoans == new HashSet<Loan>(loans)
    }

    def "should successfuly apply for loan"() {

        setup:
        def currentDate = LocalDate.now()
        def paymentDeadlineDate = currentDate.plusDays(10)

        expectedLoan = new Loan(amount: 1000, termInDays: 10, extended: false, issueDate: currentDate,
                paymentDeadlineDate: paymentDeadlineDate, charge: CALCULATED_CHARGE)

        userRepository.findOne(USER_ID) >> user
        loanChargeCalculatorService.calculateChargeFor(loanApplication) >> CALCULATED_CHARGE
        loanRepository.save(expectedLoan) >> expectedLoan

        when:
        Loan returnedLoan = loanService.applyForLoan(USER_ID, USERNAME, loanApplication, CLIENT_IP_ADDRESS)

        then:
        assertLoanAsExpected(returnedLoan, expectedLoan)

        1 * loanEventLogger.logApplication(*_) >> { arguments ->
            assertProperArgumentsWithLoanApplicationPassed(arguments)
        }

        1 * riskAnalysisService.performRiskAnalysisFor(*_) >> { arguments ->
            assertProperArgumentsWithLoanApplicationPassed(arguments)
        }

        then:

        1 * loanEventLogger.logIssuance(*_) >> { arguments ->
            assertLoanAsExpected(arguments[0], expectedLoan)
            assertProperClientAddressIpAndUserName(arguments)
        }

        1 * loanRepository.save(*_) >> { arguments ->
            assertLoanAsExpected(arguments[0], expectedLoan)
        }
    }

    def "should properly extend loan"() {


        given:
        def extendedPaymentDeadlineDate = loanToExtend.paymentDeadlineDate.plusWeeks(1)

        expectedLoan = new Loan(amount: loanToExtend.amount, extended: true,
                paymentDeadlineDate: extendedPaymentDeadlineDate, charge: EXTENDED_CHARGE)

        loanRepository.getOne(loanToExtend.id) >> loanToExtend
        loanChargeCalculatorService.calculateExtensionCharge(loanToExtend.charge) >> EXTENDED_CHARGE
        loanRepository.save(_) >> {arguments -> arguments[0]}

        when:
        Loan returnedLoan = loanService.extendLoan(USER_ID, loanToExtend.id)

        then:
        assertExtendedLoanAsExpected(returnedLoan, expectedLoan)
    }

    private void assertProperArgumentsWithLoanApplicationPassed(def arguments) {
        assert arguments[0] == loanApplication
        assertProperClientAddressIpAndUserName(arguments)
    }

    private void assertProperClientAddressIpAndUserName(def arguments) {
        assert arguments[1] == CLIENT_IP_ADDRESS
        assert arguments[2] == USERNAME
    }

    private void assertLoanAsExpected(Loan foundLoan, Loan expectedLoan) {
        assert foundLoan.amount == expectedLoan.amount
        assert foundLoan.termInDays == expectedLoan.termInDays
        assert foundLoan.extended == expectedLoan.extended
        assert foundLoan.charge == expectedLoan.charge
        assert foundLoan.issueDate == expectedLoan.issueDate
        assert foundLoan.paymentDeadlineDate == expectedLoan.paymentDeadlineDate
    }

    private void assertExtendedLoanAsExpected(Loan extendedLoan, Loan expectedLoan) {
        assert extendedLoan.amount == expectedLoan.amount
        assert extendedLoan.extended == expectedLoan.extended
        assert extendedLoan.charge == expectedLoan.charge
        assert extendedLoan.paymentDeadlineDate == expectedLoan.paymentDeadlineDate
    }
}
