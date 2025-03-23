package dasturlash.uz.service;

import dasturlash.uz.dto.request.RegistrationDTO;
import dasturlash.uz.dto.response.JwtResponseDTO;
import dasturlash.uz.dto.response.ResponseDTO;
import dasturlash.uz.entity.User;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.enums.UserRole;
import dasturlash.uz.exceptions.auth_related.UnauthorizedException;
import dasturlash.uz.repository.UserRepository;
import dasturlash.uz.security.CustomUserDetails;
import dasturlash.uz.shared.LoginValidationService;
import dasturlash.uz.util.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginValidationService loginValidationService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ResourceBundleService resourceBundleService;
    private final WebClient webClient;
    private final UserRepository userRepository;

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

    public JwtResponseDTO register(@Valid RegistrationDTO requestDTO) {

        // Check if the login is an email or a phone number

        // if it is an email, then call registerViaEmail method

        // if it is a phone number, then call registerViaPhoneNumber method

        // if it is neither email nor phone number, then throw an exception

        return null;
    }

    public JwtResponseDTO loginByBot(String code) {


        // TO DO

        // user table should be changed to include firstName and lastName

        // on nest js part include username to db

        // before saving phoneNumber to db, make sure it has +


        ResponseDTO responseDto = webClient.post()
                .bodyValue(Map.of("code", code))
                .retrieve()
                .bodyToMono(ResponseDTO.class)
                .block();

        if (responseDto == null || !responseDto.isValid()) {
            throw new UnauthorizedException("Code is invalid or expired");
        }

        String telegramId = responseDto.telegramId();
        String phoneNumber = responseDto.phoneNumber();
        String firstName = responseDto.firstName();
        String lastName = responseDto.lastName();

        // Example: create JWT based on phoneNumber or telegramId
        return new JwtResponseDTO(
                jwtUtil.encode(phoneNumber, UserRole.USER_ROLE),
                jwtUtil.refreshToken(phoneNumber, UserRole.USER_ROLE),
                phoneNumber,
                UserRole.USER_ROLE
        );
    }

}