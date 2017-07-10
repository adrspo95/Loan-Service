package app.configuration.security.authorization;

/**
 * Created by GW95IB on 2017-05-29.
 */
public interface AuthorizationRule {
    String IS_ADMIN = "hasRole('" + Authority.ADMIN + "')";

    String IS_USER_WITH_SPECIFIED_ID = "(isAuthenticated() and @userRepository.findUsernameOfUserWithId(#userId) == principal.username)";

    String IS_ADMIN_OR_USER_WITH_SPECIFIED_ID = IS_ADMIN + " or " + IS_USER_WITH_SPECIFIED_ID;

    String IS_ANONYMOUS = "isAnonymous()";
}
