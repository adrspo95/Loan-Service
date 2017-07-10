package app.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by GW95IB on 2017-05-26.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoAccountDefinitionException extends RuntimeException {

    private static final String MESSAGE = "The user with name (%s) has not Account definition";

    public NoAccountDefinitionException(String userName) {
        super(String.format(MESSAGE, userName));
    }
}
