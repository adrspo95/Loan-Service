package app.exception.loan;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by GW95IB on 2017-05-26.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TooHighLoanAmountException extends RuntimeException {

    private static final String MESSAGE = "The loan amount cannot be greater than %f";

    public TooHighLoanAmountException(double maxLoanAmount) {
        super(String.format(MESSAGE, maxLoanAmount));
    }
}
