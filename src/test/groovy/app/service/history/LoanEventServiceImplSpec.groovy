package app.service.history

import app.model.account.Account
import app.model.history.LoanEvent
import app.model.loan.Loan
import app.model.user.User
import app.repository.history.LoanEventRepository
import app.repository.user.UserRepository
import app.service.impl.history.LoanEventServiceImpl
import spock.lang.Specification

/**
 * Created by GW95IB on 2017-05-29.
 */
class LoanEventServiceImplSpec extends Specification {

    def static final USER_ID = 1

    def loanEventRepository = Mock(LoanEventRepository)
    def userRepository = Mock(UserRepository)

    def loanEventService = new LoanEventServiceImpl(loanEventRepository, userRepository)

    List<LoanEvent> expectedLoanEvents

    def setup() {
        def loanEvent1 = new LoanEvent(id: 1, userName: "user")
        def loanEvent2 = new LoanEvent(id: 2, userName: "user")

        expectedLoanEvents = [loanEvent1, loanEvent2]
    }

    def "should return all loanEvents"() {

        given:
        loanEventRepository.findAll() >> expectedLoanEvents

        when:
        List<LoanEvent> foundLoanEvents = loanEventService.findAll()

        then:
        foundLoanEvents == expectedLoanEvents
    }

    def "should return User's Loans"() {

        given:
        def account = new Account(emailAddress: "user", firstName: "Foo", lastName: "Bar", loanEvents: expectedLoanEvents)
        def user = new User(username: "user", id: USER_ID, account: account)

        userRepository.findOne(USER_ID) >> user

        when:
        Set<LoanEvent> foundLoansEvents = loanEventService.findAllForUserWithId(USER_ID)

        then:
        foundLoansEvents == new HashSet<Loan>(expectedLoanEvents)
    }

}
