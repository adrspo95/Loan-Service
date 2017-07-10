package app.web.exception.handler

import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Created by GW95IB on 2017-05-31.
 */
class RiskAnalysisExceptionHandlerSpec extends Specification {

    def mockedController = new MockedController()

    def globalExceptionHandler = new GlobalExceptionHandler()
    def riskAnalysisExceptionHandler = new RiskAnalysisExceptionHandler()

    def mvc = MockMvcBuilders
            .standaloneSetup(mockedController)
            .setControllerAdvice(globalExceptionHandler, riskAnalysisExceptionHandler)
            .build()

    def "should return exception info with default response status"() {

        when:
        ResultActions mvcResult = mvc.perform(get(MockedController.LOAN_REJECTION_EXCEPTION_URI))

        then:
        mvcResult
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath('$', is(equalTo(RiskAnalysisExceptionHandler.REJECION_MESSAGE))))
    }
}
