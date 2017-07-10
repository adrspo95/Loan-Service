package app.controller.account

import app.model.account.Account
import app.repository.user.UserRepository
import app.service.api.account.AccountService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import static org.hamcrest.core.IsCollectionContaining.hasItems
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Created by GW95IB on 2017-05-29.
 */
class AccountControllerSpec extends Specification {

    static final def EMAIL_ADDRESS = "admin@admin.com"

    static final def FIRST_NAME_1 = "First1"
    static final def FIRST_NAME_2 = "First2"
    static final def LAST_NAME_1 = "Last1"
    static final def LAST_NAME_2 = "Last2"

    static final def MOCK_USER_ID = 1

    static final def GET_MOCK_USER_ACCOUNT_URI = "/users/" + MOCK_USER_ID + "/account"
    static final def GET_ALL_ACCOUNTS_URI = "/accounts"
    static final def UPDATE_ACCOUNT_URI = "/users/" + MOCK_USER_ID + "/account"


    def accountService = Mock(AccountService)
    def userRepository = Mock(UserRepository)

    def accountController = new AccountController(accountService, userRepository)

    def mvc = MockMvcBuilders.standaloneSetup(accountController).build()

    def "should return response with proper account"() {

        given:
        Account account = new Account(emailAddress: EMAIL_ADDRESS)
        accountService.findAccountOfUserWithId(MOCK_USER_ID) >> account

        when:
        ResultActions mvcResult = mvc.perform(get(GET_MOCK_USER_ACCOUNT_URI))

        then:
        mvcResult
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.emailAddress', is(equalTo(EMAIL_ADDRESS))))
    }

    def "should return response with proper accounts"() {

        given:
        List<Account> accounts = [new Account(firstName: FIRST_NAME_1, lastName: LAST_NAME_1), new Account(firstName: FIRST_NAME_2, lastName: LAST_NAME_2)]
        accountService.findAll() >> accounts

        when:
        ResultActions mvcResult = mvc.perform(get(GET_ALL_ACCOUNTS_URI))

        then:
        mvcResult
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(accounts.size())))
                .andExpect(jsonPath('$[*].firstName', hasItems(FIRST_NAME_1, FIRST_NAME_2)))
                .andExpect(jsonPath('$[*].lastName', hasItems(LAST_NAME_2, LAST_NAME_2)))
    }

    def "account should be updated"() {

        given:
        def objectMapper = new ObjectMapper()
        def account = new Account(firstName: FIRST_NAME_1, lastName: LAST_NAME_1)

        1 * accountService.updateAccountOfUserWithId(_ as Account, MOCK_USER_ID) >> { arguments ->
            Account passedAccount = arguments[0]
            Integer passedUserId = arguments[1]

            assert passedAccount.firstName == FIRST_NAME_1
            assert passedAccount.lastName == LAST_NAME_1
            assert passedUserId == MOCK_USER_ID

            passedAccount
        }

        when:
        ResultActions mvcResult = mvc.perform(put(UPDATE_ACCOUNT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))

        mvcResult.andDo(print())

        then:

        mvcResult
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.firstName', is(equalTo(FIRST_NAME_1))))
                .andExpect(jsonPath('$.lastName', is(equalTo(LAST_NAME_1))))
    }
}
