package dasturlash.uz.service;

import dasturlash.uz.dto.response.JwtResponseDTO;
import dasturlash.uz.entity.User;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.enums.UserRole;
import dasturlash.uz.exceptions.auth_related.UnauthorizedException;
import dasturlash.uz.repository.UserRepository;
import dasturlash.uz.security.CustomUserDetails;
import dasturlash.uz.shared.LoginValidationService;
import dasturlash.uz.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginValidationService loginValidationService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ResourceBundleService resourceBundleService;

    public JwtResponseDTO login(String login, String password) {
        // Validate login and get the user
        User user = loginValidationService.validateLoginAndGetUser(login);

        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login, password)
            );

            if (authentication.isAuthenticated()) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                UserRole userRole = userDetails.getRole();

                return new JwtResponseDTO(
                        jwtUtil.encode(login, userRole),  // token
                        jwtUtil.refreshToken(login, userRole),
                        login,
                        userRole
                );
            }

            throw new UnauthorizedException(resourceBundleService.getMessage("login.password.wrong", LanguageEnum.uz));
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException(resourceBundleService.getMessage("login.password.wrong", LanguageEnum.uz));
        }
    }
}