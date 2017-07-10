package app.controller.loan

import app.dto.applicaion.LoanApplication
import app.model.loan.Loan
import app.repository.user.UserRepository
import app.service.api.loan.LoanService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import java.security.Principal

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import static org.hamcrest.core.IsCollectionContaining.hasItems
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Created by GW95IB on 2017-05-29.
 */
class LoanControllerSpec extends Specification {

    static final def MOCK_USER_ID = 1
    static final def MOCK_USERNAME = "user"
    static final def GET_MOCK_USER_LOANS_URI = "/users/" + MOCK_USER_ID + "/account/loans"
    static final def REQUEST_FOR_LOAN_URI = "/users/" + MOCK_USER_ID + "/account/loans"
    static final def EXTEND_LOAN_URI = "/users/" + MOCK_USER_ID + "/account/loans/" + LOAN_ID
    static final def GET_ALL_LOANS_URI = "/loans"
    static final def MOCKED_REQUESTOR_IP_ADDRESS = "127.0.0.1"

    static final def LOAN_ID = 1
    static final def AMOUNT = 1000.0d
    static final def TERM_IN_DAYS = 7

    def loanService = Mock(LoanService)
    def userRepository = Mock(UserRepository)
    def loanController = new LoanController(loanService, userRepository)

    def mvc = MockMvcBuilders.standaloneSetup(loanController).build()

    def "should return response with proper loans"() {

        given:
        Set<Loan> loans = [new Loan(amount: 1000.0), new Loan(amount: 2000.0)]
        loanService.findAllForUserWithId(MOCK_USER_ID) >> loans

        when:
        ResultActions mvcResult = mvc.perform(get(GET_MOCK_USER_LOANS_URI))

        then:
        assertSuccessResponseWithProperLoans(mvcResult, loans)
    }

    def "should return response with proper accounts"() {

        given:
        List<Loan> loans = [new Loan(amount: 1000.0), new Loan(amount: 2000.0)]
        loanService.findAll() >> loans

        when:
        ResultActions mvcResult = mvc.perform(get(GET_ALL_LOANS_URI))

        then:
        assertSuccessResponseWithProperLoans(mvcResult, loans)
    }

    def "loan should be properly requested"() {

        given:
        def objectMapper = new ObjectMapper()
        def loanApplication = new LoanApplication(amount: AMOUNT, termInDays: TERM_IN_DAYS)
        def loan = new Loan(id: LOAN_ID, amount: AMOUNT, termInDays: TERM_IN_DAYS)

        1 * loanService.applyForLoan(MOCK_USER_ID, MOCK_USERNAME, _ as LoanApplication, _ as String) >> { arguments ->
            Integer passedUserId = arguments[0]
            String principalName = arguments[1]
            LoanApplication passedLoanApplication = arguments[2]
            String hostIpAddress = arguments[3]

            assert passedUserId == MOCK_USER_ID
            assert passedLoanApplication.amount == AMOUNT
            assert passedLoanApplication.termInDays == TERM_IN_DAYS
            assert principalName == MOCK_USERNAME
            assert hostIpAddress == MOCKED_REQUESTOR_IP_ADDRESS

            loan
        }

        when:
        ResultActions mvcResult = mvc.perform(post(REQUEST_FOR_LOAN_URI)
                .principal(mockedPrincipal())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loanApplication)))


        then:
        mvcResult
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.id', is(equalTo(LOAN_ID))))
                .andExpect(jsonPath('$.amount', is(equalTo(AMOUNT))))
                .andExpect(jsonPath('$.termInDays', is(equalTo(TERM_IN_DAYS))))

    }

    def "loan should be properly extended"() {

        given:
        def loanAfterExtension = new Loan(id: LOAN_ID, amount: AMOUNT + 1000.0d, termInDays: TERM_IN_DAYS + 7)

        1 * loanService.extendLoan(MOCK_USER_ID, LOAN_ID) >> loanAfterExtension

        when:
        ResultActions mvcResult = mvc.perform(put(EXTEND_LOAN_URI))

        then:
        mvcResult
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.id', is(equalTo(LOAN_ID))))
                .andExpect(jsonPath('$.amount', is(equalTo(loanAfterExtension.amount))))
                .andExpect(jsonPath('$.termInDays', is(equalTo(loanAfterExtension.termInDays))))
    }

    private Principal mockedPrincipal() {
        return new Principal() {
            @Override
            String getName() {
                return MOCK_USERNAME
            }
        }
    }

    private assertSuccessResponseWithProperLoans(ResultActions mvcResult, Collection<Loan> loans) {
        mvcResult
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(loans.size())))
                .andExpect(jsonPath('$[*].amount', hasItems(1000.0d, 2000.0d)))
    }
}
