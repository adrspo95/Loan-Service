package app.configuration.security.authentication

import app.model.user.User
import app.repository.user.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification

/**
 * Created by GW95IB on 2017-05-31.
 */
class UserDetailsServiceImplSpec extends Specification{

    def static final USERNAME = "user"

    def userRepository = Mock(UserRepository)

    def userDetailService = new UserDetailsServiceImpl(userRepository)

    def "should return user if one exists"() {
        given:
        def user = new User(id: 1, username: USERNAME)

        userRepository.findByUsername(USERNAME) >> Optional.of(user)

        when:
        def foundUser = userDetailService.loadUserByUsername(USERNAME)

        then:
        user == foundUser
    }

    def "should throw exception if user not exists"() {
        given:
        userRepository.findByUsername(USERNAME) >> Optional.empty()

        when:
        userDetailService.loadUserByUsername(USERNAME)

        then:
        thrown(UsernameNotFoundException)

    }
}
