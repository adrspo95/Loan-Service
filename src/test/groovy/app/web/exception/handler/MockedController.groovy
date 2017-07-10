package app.web.exception.handler

import app.exception.loan.LoanApplicationRejectedException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by GW95IB on 2017-05-31.
 */
@RestController
class MockedController {

    def static final RUNTIME_EXCEPTION_URI = "/runtimeexception"
    def static final LOAN_REJECTION_EXCEPTION_URI = "/loanrejectionexception"

    @GetMapping(MockedController.RUNTIME_EXCEPTION_URI)
    void throwRuntimeException() {
        throw new RuntimeException()
    }

    @GetMapping(MockedController.LOAN_REJECTION_EXCEPTION_URI)
    void throwLoanRejectionException() {
        throw new LoanApplicationRejectedException()
    }
}
