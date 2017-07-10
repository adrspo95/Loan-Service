package app.configuration.security.authentication;

import app.configuration.security.global.SecurityProps;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by GW95IB on 2017-05-25.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    @Override public void afterPropertiesSet() throws Exception {
        setRealmName(SecurityProps.BASIC_REALM_NAME.getDisplayName());
        super.afterPropertiesSet();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @Override public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");
    }
}
