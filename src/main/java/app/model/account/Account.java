package app.model.account;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.NotBlank;

import app.model.BaseEntity;
import app.model.history.LoanEvent;
import app.model.loan.Loan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

/**
 * Created by Adrian on 2017-05-25.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

@Entity
public class Account extends BaseEntity {

    private static final String FOREIGN_KEY_NAME = "ACCOUNT_ID";

    @NotBlank
    @Size(min = 5, max = 254)
    @Column(unique = true)
    private String emailAddress;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @CreditCardNumber
    private String creditCardNumber;

    @OneToMany
    @JoinColumn(name = FOREIGN_KEY_NAME)
    @Singular
    private Set<Loan> loans;

    @OneToMany
    @JoinColumn(name = FOREIGN_KEY_NAME)
    @Singular
    private Set<LoanEvent> loanEvents;

    public void setId(Long id) {
        super.setId(id);
    }
}
