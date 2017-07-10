package app.controller.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import app.configuration.security.authorization.AuthorizationRule;
import app.model.user.User;
import app.repository.user.UserRepository;
import app.service.api.user.UserService;

import java.io.PrintStream;

/**
 * Created by GW95IB on 2017-05-25.
 */
@RestController
public class UserController {

    private static final String USERS = "/users";
    public static final String USER = USERS + "/{userId}";

    private final UserService userService;
    private final UserRepository userRepository;

    private PrintStream log = System.out;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping(USER)
    @PreAuthorize(AuthorizationRule.IS_USER_WITH_SPECIFIED_ID)
    public User findOne(@PathVariable Long userId) {
        return userService.findOne(userId);
    }

    @PostMapping(USERS)
    @PreAuthorize(AuthorizationRule.IS_ANONYMOUS)
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

}
