package app.service.impl.user;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import app.exception.user.NoAccountDefinitionException;
import app.exception.user.PasswordNotProvidedException;
import app.exception.user.UsernameInconsistencyException;
import app.model.account.Account;
import app.model.user.User;
import app.service.api.user.UserValidatorService;

/**
 * Created by GW95IB on 2017-06-02.
 */
@Service
public class UserValidatorServiceImpl implements UserValidatorService {

    @Override
    public void validateNewUser(User user) {
        validateAccount(user);
        validateUsernameConsistency(user);
        validatePassword(user);
    }

    private void validateAccount(User user) {
        if(Objects.isNull(user.getAccount())) {
            throw new NoAccountDefinitionException(user.getUsername());
        }
    }

    private void validateUsernameConsistency(User user) {
        Account account = user.getAccount();

        if(!user.getUsername().equals(account.getEmailAddress())) {
            throw new UsernameInconsistencyException(user.getUsername(), account.getEmailAddress());
        }
    }

    private void validatePassword(User user) {
        if (StringUtils.isBlank(user.getPassword())) {
            throw new PasswordNotProvidedException(user.getUsername());
        }
    }
}
