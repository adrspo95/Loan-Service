package app.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by GW95IB on 2017-05-26.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsernameInconsistencyException extends RuntimeException {

    private static final String MESSAGE = "The user with name (%s) has different email address (%s) provides in Account definition";

    public UsernameInconsistencyException(String userName, String emailAddress) {
        super(String.format(MESSAGE, userName, emailAddress));
    }
}
