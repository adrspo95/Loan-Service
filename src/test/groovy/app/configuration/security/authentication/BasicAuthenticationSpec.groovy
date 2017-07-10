package app.configuration.security.authentication

import app.Application
import app.configuration.security.authentication.config.MockedAuthenticationController
import app.configuration.security.authentication.config.MockedSecurityConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Created by GW95IB on 2017-06-01.
 */
@SpringBootTest(classes = [Application, MockedAuthenticationController, MockedSecurityConfig], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
class BasicAuthenticationSpec extends Specification {

    def static final INVALID_PASSWORD ="invalid"

    @Autowired
    WebApplicationContext wac

    def mvc
    ResultActions mvcResult

    def setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build()
    }

    def "known user should be authenticated"() {

        when:
        mvcResult = mvc.perform(get(MockedAuthenticationController.REQUIRE_AUTHENTICATION_URI)
                .with(httpBasic(MockedSecurityConfig.USERNAME, MockedSecurityConfig.PASSWORD)))
        mvcResult.andDo(print())

        then:
        mvcResult.andExpect(status().isOk())
    }

    @WithAnonymousUser
    def "anonymous user should not be authenticated"() {

        when:
        mvcResult = mvc.perform(get(MockedAuthenticationController.REQUIRE_AUTHENTICATION_URI))
        mvcResult.andDo(print())

        then:
        mvcResult.andExpect(status().isUnauthorized())
    }

    def "known user with incorrect password should not be authenticated"() {

        when:
        mvcResult = mvc.perform(get(MockedAuthenticationController.REQUIRE_AUTHENTICATION_URI)
                .with(httpBasic(MockedSecurityConfig.USERNAME, INVALID_PASSWORD)))
        mvcResult.andDo(print())

        then:
        mvcResult.andExpect(status().isUnauthorized())
    }
}
