package dasturlash.uz.shared;

import dasturlash.uz.entity.User;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.exceptions.auth_related.UnauthorizedException;
import dasturlash.uz.repository.UserRepository;
import dasturlash.uz.service.ResourceBundleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginValidationService {

    private final UserRepository userRepository;
    private final ResourceBundleService resourceBundleService;

    public User validateLoginAndGetUser(String login) {
        // Check if the login is an email or a phone number
        Optional<User> optional;
        if (isValidEmail(login)) {
            // Query by email
            optional = userRepository.findByEmailAndIsDeletedFalse(login);
        } else if (isValidPhoneNumber(login)) {
            // Query by phone number
            optional = userRepository.findByPhoneNumberAndIsDeletedFalse(login);
        } else {
            // Invalid login format
            throw new UnauthorizedException(resourceBundleService.getMessage("invalid.login.format", LanguageEnum.uz));
        }

        // Check if the user exists
        return optional.orElseThrow(() -> new UnauthorizedException(resourceBundleService.getMessage("user.not.found", LanguageEnum.uz)));
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