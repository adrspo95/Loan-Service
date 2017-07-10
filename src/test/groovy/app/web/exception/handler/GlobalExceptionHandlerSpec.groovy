package app.web.exception.handler

import app.controller.loan.LoanController
import app.exception.loan.LoanNotExtendableException
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Created by GW95IB on 2017-05-31.
 */
class GlobalExceptionHandlerSpec extends Specification {

    static final def USER_ID = 1
    static final def LOAN_ID = 1

    static final EXTEND_LOAN_URI = "/users/" + USER_ID + "/account/loans/" + LOAN_ID

    def loanController = Mock(LoanController)
    def mockedController = new MockedController()

    def globalExceptionHandler = new GlobalExceptionHandler()

    def mvc = MockMvcBuilders
            .standaloneSetup(loanController, mockedController)
            .setControllerAdvice(globalExceptionHandler)
            .build()

    def "should return exception info customized by global exception handler"() {

        given:
        loanController.extendLoan(USER_ID, LOAN_ID) >> { Long userId, Long loanId ->
            throw new LoanNotExtendableException(loanId)
        }

        when:
        ResultActions mvcResult = mvc.perform(put(EXTEND_LOAN_URI))
        mvcResult.andDo(print())

        then:
        mvcResult
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.exceptionName', is(equalTo(LoanNotExtendableException.class.simpleName))))
                .andExpect(jsonPath('$.requestUri', is(equalTo(EXTEND_LOAN_URI))))
                .andExpect(jsonPath('$.exceptionMessage', is(equalTo(String.format(LoanNotExtendableException.MESSAGE, LOAN_ID)))))
    }

    def "should return exception info with default response status"() {

        when:
        ResultActions mvcResult = mvc.perform(get(MockedController.RUNTIME_EXCEPTION_URI))
        mvcResult.andDo(print())

        then:
        mvcResult
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath('$.exceptionName', is(equalTo(RuntimeException.class.simpleName))))
    }

}
