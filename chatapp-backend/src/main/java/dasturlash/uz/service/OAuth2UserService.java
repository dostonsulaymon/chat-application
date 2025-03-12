package dasturlash.uz.service;

import dasturlash.uz.entity.User;
import dasturlash.uz.enums.GeneralStatus;
import dasturlash.uz.enums.UserRole;
import dasturlash.uz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OAuth2UserService {

    private final UserRepository userRepository;

    public User registerOAuthUser(
            OAuth2User oauthUser,
            String providerId,
            String email,
            String name,
            String provider
    ) {
        // Check if user already exists
        User existingUser = userRepository.findByOauthProviderAndOauthId(provider, providerId);

        if (existingUser != null) {
            // Update last login for existing OAuth user
            existingUser.setLastLogin(LocalDateTime.now());
            return userRepository.save(existingUser);
        }

        // Create new user if not exists
        User newUser = new User();
        newUser.setUsername(name);
        newUser.setEmail(email);
        newUser.setOauthProvider(provider);
        newUser.setOauthId(providerId);
        newUser.setUserRole(UserRole.USER_ROLE); // Default role
        newUser.setAccountStatus(GeneralStatus.ACTIVE);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        newUser.setLastLogin(LocalDateTime.now());

        return userRepository.save(newUser);
    }
}