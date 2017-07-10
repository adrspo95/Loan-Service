package app.service.loan

import app.dto.applicaion.LoanApplication
import app.service.impl.loan.LoanChargeCalculatorServiceImpl
import spock.lang.Specification

/**
 * Created by GW95IB on 2017-07-10.
 */
class LoanChargeCalculatorServiceImplSpec extends Specification {

    static final def AMOUNT = 1000.00d
    static final def TERM_IN_DAYS = 10

    static final def COMMISSION_RATE = 0.10d
    static final def INTEREST_RATE = 0.01d
    static final def INTERESTS_EXTEND_FACTOR = 0.10d

    static final def EXPECTED_CHARGE = (AMOUNT * COMMISSION_RATE) + (AMOUNT * INTEREST_RATE * TERM_IN_DAYS)
    static final def EXPECTED_EXTENSION_CHARGE = AMOUNT * INTERESTS_EXTEND_FACTOR

    def loanChargeCalculatorService = new LoanChargeCalculatorServiceImpl(COMMISSION_RATE, INTEREST_RATE, INTERESTS_EXTEND_FACTOR)

    def "charge should be properly calculated for loan application"() {

        given:
        def loanApplication = new LoanApplication(amount: AMOUNT, termInDays: TERM_IN_DAYS)

        when:
        Double calculatedCharge = loanChargeCalculatorService.calculateChargeFor(loanApplication)

        then:
        calculatedCharge == EXPECTED_CHARGE
    }

    def "extension charge should be calculated properly"() {

        when:
        Double calculatedExtensionCharge = loanChargeCalculatorService.calculateExtensionCharge(AMOUNT)

        then:
        calculatedExtensionCharge == EXPECTED_EXTENSION_CHARGE
    }


}
