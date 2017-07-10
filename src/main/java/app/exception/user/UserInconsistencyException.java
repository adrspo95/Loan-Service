package app.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by GW95IB on 2017-05-26.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserInconsistencyException extends RuntimeException {

    private static final String MESSAGE = "The user id (%d) from URI is not the requestor's id";

    public UserInconsistencyException(Long uriUserId) {
        super(String.format(MESSAGE, uriUserId));
    }
}
