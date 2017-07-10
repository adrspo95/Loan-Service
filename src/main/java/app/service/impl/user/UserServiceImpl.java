package app.service.impl.user;

import app.configuration.security.authorization.Role;
import app.exception.user.NoAccountDefinitionException;
import app.exception.user.PasswordNotProvidedException;
import app.exception.user.UsernameInconsistencyException;
import app.model.account.Account;
import app.model.user.User;
import app.repository.user.UserRepository;
import app.service.api.user.UserService;
import app.service.api.user.UserValidatorService;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Set;

/**
 * Created by GW95IB on 2017-05-26.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidatorService userValidatorService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserValidatorService userValidatorService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userValidatorService = userValidatorService;
    }

    @Override public User findOne(Long userId) {
        return userRepository.findOne(userId);
    }

    @Override public User create(User user) {
        userValidatorService.validateNewUser(user);

        Account account = prepareAccountFor(user);
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        Set<Role> grantedRoles = ImmutableSet.of(Role.USER);

        User userToBeSaved = User.builder()
                .username(user.getUsername())
                .password(encodedPassword)
                .account(account)
                .roles(grantedRoles)
                .build();

        return userRepository.save(userToBeSaved);
    }

    private Account prepareAccountFor(User user) {
        Account account = user.getAccount();

        return Account.builder()
                .emailAddress(account.getEmailAddress())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .creditCardNumber(account.getCreditCardNumber())
                .build();
    }
}
