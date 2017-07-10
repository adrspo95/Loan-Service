package app.service.api.user;

import app.model.user.User;

import java.util.List;

/**
 * Created by GW95IB on 2017-05-26.
 */
public interface UserService {

    User findOne(Long userId);

    User create(User user);
}
