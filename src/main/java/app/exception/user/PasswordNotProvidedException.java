package app.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by GW95IB on 2017-05-26.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordNotProvidedException extends RuntimeException {

    private static final String MESSAGE = "The password is not provided for user with name (%s)";

    public PasswordNotProvidedException(String username) {
        super(String.format(MESSAGE, username));
    }
}
