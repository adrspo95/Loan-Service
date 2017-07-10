package app.web.exception.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import app.exception.loan.LoanApplicationRejectedException;

/**
 * Created by GW95IB on 2017-05-26.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RiskAnalysisExceptionHandler {

    public static final String REJECION_MESSAGE = "Loan Application was rejected";

    @ExceptionHandler(LoanApplicationRejectedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleRiskAnalysisFailure() {
        return REJECION_MESSAGE;
    }

}
