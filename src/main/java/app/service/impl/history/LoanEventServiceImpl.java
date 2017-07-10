package app.service.impl.history;

import java.util.List;
import java.util.Set;

import app.repository.history.LoanEventRepository;
import app.repository.user.UserRepository;
import org.springframework.stereotype.Service;

import app.model.history.LoanEvent;
import app.service.api.history.LoanEventService;

/**
 * Created by GW95IB on 2017-05-26.
 */
@Service
public class LoanEventServiceImpl implements LoanEventService {

    private final LoanEventRepository loanEventRepository;
    private final UserRepository userRepository;

    public LoanEventServiceImpl(
            LoanEventRepository loanEventRepository,
            UserRepository userRepository) {
        this.loanEventRepository = loanEventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<LoanEvent> findAll() {
        return loanEventRepository.findAll();
    }

    @Override
    public Set<LoanEvent> findAllForUserWithId(Long userId) {
        return userRepository.findOne(userId).getAccount().getLoanEvents();
    }
}
