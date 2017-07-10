package app.repository.account;

import app.model.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Adrian on 2017-05-25.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
