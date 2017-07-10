package app.exception.loan;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by GW95IB on 2017-05-26.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TooShortLoanTermException extends RuntimeException {

    private static final String MESSAGE = "The loan term cannot be shorter than %d days";

    public TooShortLoanTermException(Integer minLoanTerm) {
        super(String.format(MESSAGE, minLoanTerm));
    }
}
