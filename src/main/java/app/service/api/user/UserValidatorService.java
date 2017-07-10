package app.service.api.user;

import app.model.user.User;

/**
 * Created by GW95IB on 2017-06-02.
 */
public interface UserValidatorService {
    void validateNewUser(User user);
}
