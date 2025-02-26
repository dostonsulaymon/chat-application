package dasturlash.uz.security;

import dasturlash.uz.entity.User;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.exceptions.auth_related.UnauthorizedException;
import dasturlash.uz.repository.UserRepository;
import dasturlash.uz.security.CustomUserDetails;
import dasturlash.uz.service.ResourceBundleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ResourceBundleService resourceBundleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Check if the login is an email or a phone number
        Optional<User> optional;
        if (isValidEmail(username)) {
            // Query by email
            optional = userRepository.findByEmailAndIsDeletedFalse(username);
        } else if (isValidPhoneNumber(username)) {
            // Query by phone number
            optional = userRepository.findByPhoneNumberAndIsDeletedFalse(username);
        } else {
            // Invalid login format
            log.info("Invalid login format: {}", username);
            throw new UnauthorizedException(resourceBundleService.getMessage("invalid.login.format", LanguageEnum.uz));
        }

        // Check if the user exists
        if (optional.isEmpty()) {
            log.info("User not found for login: {}", username);
            throw new UnauthorizedException(resourceBundleService.getMessage("user.not.found", LanguageEnum.uz));
        }

        User user = optional.get();
        return new CustomUserDetails(user);
    }

    // Method to check if the login is a valid email
    private boolean isValidEmail(String login) {
        // Simple regex for email validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return login.matches(emailRegex);
    }

    // Method to check if the login is a valid phone number
    private boolean isValidPhoneNumber(String login) {
        // Simple regex for phone number validation (adjust based on your requirements)
        String phoneRegex = "^\\+?[0-9]{10,13}$"; // Example: +998901234567 or 998901234567
        return login.matches(phoneRegex);
    }
}