package dasturlash.uz.security;

import dasturlash.uz.entity.User;
import dasturlash.uz.enums.GeneralStatus;
import dasturlash.uz.enums.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetails implements UserDetails {
    private final UUID id;
    private final String name;
    private final GeneralStatus status;
    private final String login;
    private final String password;
    private final UserRole role;

    public CustomUserDetails(User user) {
        this.id = user.getUserId();
        this.name = user.getUsername();
        this.password = user.getPasswordHash();
        this.status = user.getAccountStatus();
        this.role = user.getUserRole();

        // Allow login with either email or phone number
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            this.login = user.getEmail();
        } else if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()) {
            this.login = user.getPhoneNumber();
        } else {
            throw new IllegalStateException("User must have either an email or a phone number.");
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;  // Now supports both email and phone number
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
        return status == GeneralStatus.ACTIVE;
    }
}
