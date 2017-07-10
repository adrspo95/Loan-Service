package app.repository.user;

import app.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by GW95IB on 2017-05-25.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("SELECT user.username FROM User user WHERE user.id = (:id)")
    String findUsernameOfUserWithId(@Param("id") Long id);
}
