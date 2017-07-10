package app.repository.loan;

import app.model.loan.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Adrian on 2017-05-25.
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
}
