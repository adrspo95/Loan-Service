package app.service.api.history;

import app.model.history.LoanEvent;

import java.util.List;
import java.util.Set;

/**
 * Created by GW95IB on 2017-05-26.
 */
public interface LoanEventService {
    List<LoanEvent> findAll();

    Set<LoanEvent> findAllForUserWithId(Long userId);
}
