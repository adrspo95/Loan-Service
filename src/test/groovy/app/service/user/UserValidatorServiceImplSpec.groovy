package app.service.user

import app.exception.user.NoAccountDefinitionException
import app.exception.user.PasswordNotProvidedException
import app.exception.user.UsernameInconsistencyException
import app.model.account.Account
import app.model.user.User
import app.service.impl.user.UserValidatorServiceImpl
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by GW95IB on 2017-06-02.
 */
class UserValidatorServiceImplSpec extends Specification {

    def static final USERNAME = "user"

    @Shared def userValidatorService = new UserValidatorServiceImpl()

    def "validation of user with proper state should pass"() {

        given:
        def account = new Account(emailAddress: USERNAME, firstName: "Foo", lastName: "Bar", creditCardNumber: "2345234523")
        def user = new User(username: USERNAME, password: "password", account: account)

        when:
        userValidatorService.validateNewUser(user)

        then:
        noExceptionThrown()
    }

    def "user without account should not pass validation"() {

        given:
        def user = new User(username: USERNAME, password: "password")

        when:
        userValidatorService.validateNewUser(user)

        then:
        thrown(NoAccountDefinitionException)
    }

    def "user without password should not pass validation"() {

        given:
        def account = new Account(emailAddress: USERNAME)
        def user = new User(username: USERNAME, account: account)

        when:
        userValidatorService.validateNewUser(user)

        then:
        thrown(PasswordNotProvidedException)
    }

    def "user with inconsistent username and email should not pass validation"() {

        given:
        def account = new Account(emailAddress: USERNAME)
        def user = new User(username: USERNAME + "inconsistent_part", password: "password", account: account)

        when:
        userValidatorService.validateNewUser(user)

        then:
        thrown(UsernameInconsistencyException)
    }
}
