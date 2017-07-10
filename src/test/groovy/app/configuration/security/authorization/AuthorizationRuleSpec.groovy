package app.configuration.security.authorization

import app.Application
import app.model.user.User
import app.repository.user.UserRepository
import app.service.api.loan.LoanService
import app.service.api.user.UserService
import com.google.common.collect.ImmutableSet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.mockito.Mockito.when
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Created by GW95IB on 2017-05-31.
 */
@SpringBootTest(classes = Application, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
class AuthorizationRuleSpec extends Specification {

    def static final CLIENT_USER_ID = 1
    def static final CLIENT_USERNAME = "client"

    def static final ADMIN_USER_ID = 2
    def static final ADMIN_USERNAME = "admin"

    def static final USERS_URI = "/users"
    def static final LOANS_URI = "/loans"

    @Autowired
    WebApplicationContext wac

    @MockBean
    UserService userService

    @MockBean
    LoanService loanService

    @MockBean
    UserRepository userRepository

    def mvc
    ResultActions mvcResult

    def setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build()

        when(userRepository.findUsernameOfUserWithId(CLIENT_USER_ID)).thenReturn(CLIENT_USERNAME)
        when(userRepository.findUsernameOfUserWithId(ADMIN_USER_ID)).thenReturn(ADMIN_USERNAME)
    }

    @WithAnonymousUser
    def "is anonymous rule should permit request for unauthenticated users"() {

        when:
        mvcResult = mvc.perform(post(USERS_URI).contentType(MediaType.APPLICATION_JSON)
                .content('{"id": "' + CLIENT_USER_ID + '"}'))
        mvcResult.andDo(print())

        then:
        mvcResult.andExpect(status().isOk())
    }

    def "is anonymous rule should deny request for unauthenticated users"(MockHttpServletRequestBuilder request) {

        when:
        mvcResult = mvc.perform(request.contentType(MediaType.APPLICATION_JSON)
                .content('{"id": "' + CLIENT_USER_ID + '"}'))
        mvcResult.andDo(print())

        then:
        mvcResult.andExpect(status().isForbidden())

        where:
        request << [
                post(USERS_URI).with(user("client").roles(Role.USER.name())),
                post(USERS_URI).with(user("admin").roles(Role.ADMIN.name()))
        ]
    }

    @WithMockUser(roles = "ADMIN")
    def "is admin rule should permit request for admin"() {
        when:
        mvcResult = mvc.perform(get(LOANS_URI))
        mvcResult.andDo(print())

        then:
        mvcResult.andExpect(status().isOk())
    }

    def "is admin rule should deny request for non admin and anonymous users"(MockHttpServletRequestBuilder request) {

        when:
        int status = mvc.perform(request).andReturn().getResponse().getStatus()

        then:
        status == HttpStatus.UNAUTHORIZED.value() || status == HttpStatus.FORBIDDEN.value()

        where:
        request << [get(LOANS_URI), get(LOANS_URI).with(user("client").roles(Role.USER.name()))]
    }

    def "is user with specified id rule should permit request only for user with specified id"() {

        when:
        mvcResult = mvc.perform(get(USERS_URI + "/" + CLIENT_USER_ID).with(user(withIdAndNameAndRole(CLIENT_USER_ID, CLIENT_USERNAME, Role.USER))))

        then:
        mvcResult.andExpect(status().isOk())

        when:
        mvcResult = mvc.perform(get(USERS_URI + "/" + CLIENT_USER_ID).with(user(withIdAndNameAndRole(ADMIN_USER_ID, ADMIN_USERNAME, Role.ADMIN))))

        then:
        mvcResult.andExpect(status().isForbidden())

        when:
        mvcResult = mvc.perform(get(USERS_URI + "/" + CLIENT_USER_ID))
        mvcResult.andDo(print())

        then:
        mvcResult.andExpect(status().isUnauthorized())


    }

    def "is admin or user with specified id rule should permit request for admin or user with specified id"() {

        when:
        mvcResult = mvc.perform(get(USERS_URI + "/" + CLIENT_USER_ID + "/account" + LOANS_URI)
                .with(user(withIdAndNameAndRole(ADMIN_USER_ID, ADMIN_USERNAME, Role.ADMIN))))

        then:
        mvcResult.andExpect(status().isOk())

        when:
        mvcResult = mvc.perform(get(USERS_URI + "/" + CLIENT_USER_ID + "/account" + LOANS_URI)
                .with(user(withIdAndNameAndRole(CLIENT_USER_ID, CLIENT_USERNAME, Role.USER))))

        then:
        mvcResult.andExpect(status().isOk())
    }

    def "is admin or user with specified id rule should deny request for non admin user with another id or anonymous user"(MockHttpServletRequestBuilder request) {

        when:
        int status = mvc.perform(request).andReturn().getResponse().getStatus()

        then:
        status == HttpStatus.UNAUTHORIZED.value() || status == HttpStatus.FORBIDDEN.value()

        where:
        request << [get(USERS_URI + "/" + ADMIN_USER_ID + "/account" + LOANS_URI)
                            .with(user(withIdAndNameAndRole(CLIENT_USER_ID, CLIENT_USERNAME, Role.USER))),
                    get(USERS_URI + "/" + CLIENT_USER_ID + "/account" + LOANS_URI)
        ]

    }

    private UserDetails withIdAndNameAndRole(int id, String username, Role role) {
        UserDetails clientUserDetails = User.builder()
                .username(username)
                .roles(ImmutableSet.of(role))
                .build()

        clientUserDetails.setId(id)

        return clientUserDetails
    }
}
