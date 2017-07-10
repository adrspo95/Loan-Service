package app.configuration.security.authentication.config

import app.configuration.security.authorization.Role
import app.configuration.security.global.SecurityConfig
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder

/**
 * Created by GW95IB on 2017-06-01.
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
class MockedSecurityConfig extends SecurityConfig {

    def static final USERNAME = "mock"
    def static final PASSWORD = "mock"

    @Override
    void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth)
        auth.inMemoryAuthentication().withUser(USERNAME).password(PASSWORD).roles(Role.USER.name())
    }
}
