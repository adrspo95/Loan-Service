package app.configuration.security.authentication.config

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by GW95IB on 2017-06-01.
 */
@RestController
class MockedAuthenticationController {

    public static final REQUIRE_AUTHENTICATION_URI = "/authenticated"

    @GetMapping(MockedAuthenticationController.REQUIRE_AUTHENTICATION_URI)
    @PreAuthorize("isAuthenticated()")
    void permitAuthenticatedUsers() {}
}
