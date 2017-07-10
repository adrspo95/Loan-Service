package app.repository.history;

import app.model.history.LoanEvent;
import app.model.history.LoanEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Adrian on 2017-05-25.
 */
@Repository
public interface LoanEventRepository extends JpaRepository<LoanEvent, Long> {

    Integer countByTypeAndClientIpAddressAndDateAfter(LoanEventType type, String clientIpAddress, LocalDateTime date);
}
