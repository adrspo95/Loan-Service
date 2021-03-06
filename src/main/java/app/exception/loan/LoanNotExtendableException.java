package app.exception.loan;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by GW95IB on 2017-05-26.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LoanNotExtendableException extends RuntimeException {

    public static final String MESSAGE = "The loan with id (%d) is not extendable due to too high loan amount";

    public LoanNotExtendableException(Long loanId) {
        super(String.format(MESSAGE, loanId));
    }
}
