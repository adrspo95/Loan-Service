package app.service.account

import app.model.account.Account
import app.model.loan.Loan
import app.model.user.User
import app.repository.account.AccountRepository
import app.repository.user.UserRepository
import app.service.impl.account.AccountServiceImpl
import spock.lang.Specification

/**
 * Created by GW95IB on 2017-05-29.
 */
class AccountServiceImplSpec extends Specification {

    def accountRepository = Mock(AccountRepository)
    def userRepository = Mock(UserRepository)

    def accountService = new AccountServiceImpl(accountRepository, userRepository)

    def "should return all accounts"() {

        given:
        def account1 = new Account(id: 1, emailAddress: "user")
        def account2 = new Account(id: 2, emailAddress: "admin")

        accountRepository.findAll() >> [account1, account2]

        when:
        List<Account> foundAccounts = accountService.findAll()

        then:
        foundAccounts == [account1, account2]
    }

    def "should return User's Account"() {

        given:
        Account account = new Account(emailAddress: "user@user.com", firstName: "Foo", lastName: "Bar")
        User user = new User(username: "user", id: 1, account: account)

        userRepository.findOne(user.id) >> user

        when:
        Account foundAccount = accountService.findAccountOfUserWithId(user.id)

        then:
        foundAccount == account
    }

    def "should update only appropriate Account's fields"() {

        given:
        Account account = new Account(emailAddress: "user@user.com", firstName: "Foo", lastName: "Bar")
        Account updateAccountData = new Account(emailAddress: "foo@bar.com", firstName: "Bar",
                loans: [new Loan(id: 1), new Loan(id: 2), new Loan(id: 3)])
        User user = new User(username: "user", id: 1, account: account)

        userRepository.findOne(user.id) >> user

        when:
        accountService.updateAccountOfUserWithId(updateAccountData, user.id)

        then:
        1 * accountRepository.save(_) >> { arguments ->

            Account passedAccount = arguments[0]

            assert passedAccount.emailAddress == "user@user.com"
            assert passedAccount.firstName == "Bar"
            assert passedAccount.loans == null
        }
    }
}
