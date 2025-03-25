package dasturlash.uz.service;

import dasturlash.uz.dto.request.RegistrationDTO;
import dasturlash.uz.dto.response.EmailMessageDTO;
import dasturlash.uz.dto.response.JwtResponseDTO;
import dasturlash.uz.dto.response.ResponseDTO;
import dasturlash.uz.entity.User;
import dasturlash.uz.enums.GeneralStatus;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginValidationService loginValidationService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ResourceBundleService resourceBundleService;
    private final WebClient webClient;
    private final UserRepository userRepository;
    private final EmailSendingService  emailSendingService;

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

    public String register(@Valid RegistrationDTO requestDTO) {

        // Check if the login is an email or a phone number



        // if it is an email, then call registerViaEmail method

        // if it is a phone number, then call registerViaPhoneNumber method

        // if it is neither email nor phone number, then throw an exception

        int code = (int) (Math.random() * 900000) + 100000;

        EmailMessageDTO emailMessageDTO = new EmailMessageDTO();

        emailMessageDTO.setTo(requestDTO.login());
        emailMessageDTO.setSubject("Registration code");
        emailMessageDTO.setText("Your registration code is: " + code);

       String message =  emailSendingService.sendSimpleMessage(emailMessageDTO);


        return message;
    }

    public JwtResponseDTO loginByBot(String code) {

        // Call external service to get Telegram user data
        ResponseDTO responseDto = webClient.post()
                .bodyValue(Map.of("code", code))
                .retrieve()
                .bodyToMono(ResponseDTO.class)
                .block();

        if (responseDto == null || !responseDto.isValid()) {
            throw new UnauthorizedException("Code is invalid or expired");
        }

        // Normalize phone number
        String phoneNumber = responseDto.phoneNumber();
        if (phoneNumber != null && !phoneNumber.startsWith("+")) {
            phoneNumber = "+" + phoneNumber;
        }

        String telegramId = responseDto.telegramId();
        String firstName = responseDto.firstName();
        String lastName = responseDto.lastName();
        String username = responseDto.username();

        // Check if user already exists by phone number
        Optional<User> existingUserOpt = userRepository.findByPhoneNumberAndIsDeletedFalse(phoneNumber);


        String finalPhoneNumber = phoneNumber;
        User user = existingUserOpt.orElseGet(() -> {
            // If user doesn't exist, create new
            User newUser = new User();
            newUser.setPhoneNumber(finalPhoneNumber);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setUsername(username);
            newUser.setUserRole(UserRole.USER_ROLE);
            newUser.setAccountStatus(GeneralStatus.ACTIVE);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(newUser);
        });

        // Generate and return JWT response
        return new JwtResponseDTO(
                jwtUtil.encode(phoneNumber, user.getUserRole()),
                jwtUtil.refreshToken(phoneNumber, user.getUserRole()),
                phoneNumber,
                user.getUserRole()
        );
    }

}