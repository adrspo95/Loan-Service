package app.service.user

import app.model.account.Account
import app.model.history.LoanEvent
import app.model.loan.Loan
import app.model.user.User
import app.repository.user.UserRepository
import app.service.api.user.UserValidatorService
import app.service.impl.user.UserServiceImpl
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by GW95IB on 2017-05-29.
 */
class UserServiceImplSpec extends Specification {

    def static final USERNAME = "user@user.com"
    def static final USER_ID = 1
    def static final ENCODED_PASSWORD = "asdn084f0893vweyfi289"

    @Shared def userRepository = Mock(UserRepository)
    @Shared def passwordEncoder = Mock(BCryptPasswordEncoder)
    @Shared def userValidatorService = Mock(UserValidatorService)

    def userService = new UserServiceImpl(userRepository, passwordEncoder, userValidatorService)

    @Shared User user
    @Shared Account expectedAccount

    def setupSpec() {
        expectedAccount = new Account(emailAddress: USERNAME, firstName: "Foo", lastName: "Bar", creditCardNumber: "2345234523",
        loans: [new Loan(amount: 1000)], loanEvents: [new LoanEvent(amount: 1000)])

        user = new User(id: USER_ID, username: USERNAME, password: "password", account: expectedAccount)

        passwordEncoder.encode(user.password) >> ENCODED_PASSWORD
        userRepository.findOne(USER_ID) >> user
        userRepository.save(_) >> {arguments -> arguments[0]}
    }

    def "should return user with specified id"() {

        when:
        User foundUser = userService.findOne(USER_ID)

        then:
        foundUser == user
    }

    def "should properly create user"() {

        when:
        User returnedUser = userService.create(user)
        Account returnedAccount = returnedUser.account

        then:
        returnedUser.username == USERNAME
        returnedUser.password == ENCODED_PASSWORD

        returnedAccount.emailAddress == expectedAccount.emailAddress
        returnedAccount.firstName == expectedAccount.firstName
        returnedAccount.lastName == expectedAccount.lastName
        returnedAccount.creditCardNumber == expectedAccount.creditCardNumber
    }

    def "should ignore disallowed properties"() {

        when:
        User returnedUser = userService.create(user)
        Account returnedAccount = returnedUser.account

        then:
        returnedUser.id == null

        returnedAccount.loans.isEmpty()
        returnedAccount.loanEvents.isEmpty()
    }
}
