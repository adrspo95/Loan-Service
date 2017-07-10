package app.controller.user

import app.model.user.User
import app.repository.user.UserRepository
import app.service.api.user.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Shared
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Created by GW95IB on 2017-05-29.
 */
class UserControllerSpec extends Specification {

    static final def MOCK_USER_ID = 1
    static final def GET_MOCK_USER_URI = "/users/" + MOCK_USER_ID
    static final def CREATE_USER_URI = "/users"

    def userService = Mock(UserService)
    def userRepository = Mock(UserRepository)
    def userController = new UserController(userService, userRepository)

    def mvc = MockMvcBuilders.standaloneSetup(userController).build()

    @Shared
    def user

    def setup() {
        user = new User(username: "admin", id: MOCK_USER_ID)
    }

    def "should return response with proper user"() {

        given:
        userService.findOne(MOCK_USER_ID) >> user

        when:
        ResultActions mvcResult = mvc.perform(get(GET_MOCK_USER_URI))
        mvcResult.andDo(print())

        then:
        assertSuccessResponseWithProperUser(mvcResult)
    }

    def "user should be properly created"() {

        given:
        def objectMapper = new ObjectMapper();

        1 * userService.create(_ as User) >> { arguments ->
            User passedUser = arguments[0]

            assert passedUser.id == user.id
            assert passedUser.username == user.username

            user
        }

        when:
        ResultActions mvcResult = mvc.perform(post(CREATE_USER_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))

        then:
        assertSuccessResponseWithProperUser(mvcResult)

    }

    private assertSuccessResponseWithProperUser(ResultActions mvcResult) {
        mvcResult
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.username', is(equalTo("admin"))))
                .andExpect(jsonPath('$.id', is(equalTo(MOCK_USER_ID))))
    }
}
