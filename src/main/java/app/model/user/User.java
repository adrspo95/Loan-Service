package app.model.user;

import java.util.Collection;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;

import app.configuration.security.authorization.Role;
import app.configuration.security.global.SecurityProps;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import app.model.account.Account;
import app.model.BaseEntity;
import lombok.Getter;

import static java.util.stream.Collectors.toSet;

/**
 * Created by Adrian on 2017-05-25.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder

@Entity
@JsonIgnoreProperties(value = {"authorities"})
public class User extends BaseEntity implements UserDetails {

    private static final String FOREIGN_KEY_NAME = "USER_ID";

    private final static String ROLE_PREFIX = SecurityProps.ROLE_PREFIX.getDisplayName();

    @NotBlank @Size(min = 5, max = 254) @Column(unique = true)
    private String username;

    @NotBlank @Length(max = 60, min = 60)
    private String password;

    @ElementCollection
    @CollectionTable(joinColumns = {@JoinColumn(name = FOREIGN_KEY_NAME)})
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Getter
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Account account;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(ROLE_PREFIX + r.name()))
                .collect(toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setId(Long id) {
        super.setId(id);
    }
}
