package app.model.history;

import app.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by Adrian on 2017-05-24.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder

@Entity
public class LoanEvent extends BaseEntity {

    @NotNull
    private LoanEventType type;

    private Long loanId;

    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private Double amount;

    @NotNull
    private Integer termInDays;

    @NotNull
    private LocalDateTime date;

    @NotBlank @Column(unique = true)
    private String userName;

    @NotBlank
    private String clientIpAddress;
}
