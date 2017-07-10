package app.dto.applicaion;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Adrian on 2017-05-24.
 */
@NoArgsConstructor
@Getter
public class LoanApplication {

    private Double amount;

    private Integer termInDays;

}
