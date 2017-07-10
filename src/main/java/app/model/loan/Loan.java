package app.model.loan;

import app.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Created by Adrian on 2017-05-24.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)

@Entity
public class Loan extends BaseEntity {

    @NotNull @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private Double amount;

    @NotNull @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private Double charge;

    @NotNull
    private Integer termInDays;

    @NotNull
    private Boolean extended;

    @NotNull
    private LocalDate issueDate;

    @NotNull
    private LocalDate paymentDeadlineDate;

}
