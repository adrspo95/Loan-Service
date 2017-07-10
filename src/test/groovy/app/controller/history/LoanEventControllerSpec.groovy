package app.controller.history

import app.model.history.LoanEvent
import app.repository.user.UserRepository
import app.service.api.history.LoanEventService
import org.hamcrest.collection.IsCollectionWithSize
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import static org.hamcrest.core.IsCollectionContaining.hasItems
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Created by GW95IB on 2017-05-29.
 */
class LoanEventControllerSpec extends Specification {

    final static def MOCK_USER_ID = 1
    final static def GET_MOCK_USER_LOAN_EVENTS_URI = "/users/" + MOCK_USER_ID + "/account/history"
    static final def GET_ALL_LOAN_EVENTS = "/history"

    def loanEventService = Mock(LoanEventService)
    def userRepository = Mock(UserRepository)
    def loanEventController = new LoanEventController(loanEventService, userRepository)

    def mvc = MockMvcBuilders.standaloneSetup(loanEventController).build()

    def "should return response with proper loan events"() {

        given:
        Set<LoanEvent> loanEvents = [new LoanEvent(loanId: 1), new LoanEvent(loanId: 2)]
        loanEventService.findAllForUserWithId(MOCK_USER_ID) >> loanEvents

        when:
        ResultActions mvcResult = mvc.perform(get(GET_MOCK_USER_LOAN_EVENTS_URI))

        then:
        assertSuccessResponseWithProperLoanEvents(mvcResult, loanEvents)
    }

    def "should return response with proper accounts"() {

        given:
        List<LoanEvent> loanEvents = [new LoanEvent(loanId: 1), new LoanEvent(loanId: 2)]
        loanEventService.findAll() >> loanEvents

        when:
        ResultActions mvcResult = mvc.perform(get(GET_ALL_LOAN_EVENTS))

        then:
        assertSuccessResponseWithProperLoanEvents(mvcResult, loanEvents)
    }

    private assertSuccessResponseWithProperLoanEvents(ResultActions mvcResult, Collection<LoanEvent> loanEvents) {
        mvcResult
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(loanEvents.size())))
                .andExpect(jsonPath('$[*].loanId', hasItems(1, 2)))
    }
}
