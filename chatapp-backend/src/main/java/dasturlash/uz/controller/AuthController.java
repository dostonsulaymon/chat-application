package dasturlash.uz.controller;

import dasturlash.uz.dto.request.LoginByBotDTO;
import dasturlash.uz.dto.request.LoginDTO;
import dasturlash.uz.dto.request.RegistrationDTO;
import dasturlash.uz.dto.response.JwtResponseDTO;
import dasturlash.uz.dto.response.LoginResponseDTO;
import dasturlash.uz.service.AuthService;
import dasturlash.uz.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginDTO loginDTO) {
        JwtResponseDTO jwtResponse = authService.login(loginDTO.login(), loginDTO.password());
        String message = "You have successfully logged in!";
        return ResponseEntity.ok(new LoginResponseDTO(message, jwtResponse));
    }

    @PostMapping("/verify")
    public ResponseEntity<JwtResponseDTO> loginByBot(@RequestBody LoginByBotDTO requestDTO) {
        return ResponseEntity.ok(authService.loginByBot(requestDTO.code()));
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        tokenBlacklistService.blacklistToken(token); // Add token to blacklist
        log.info("Logged out of token: {}", token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/registration")
    public ResponseEntity<JwtResponseDTO> registrationViaEmail(@RequestBody @Valid RegistrationDTO requestDTO) {
        return ResponseEntity.ok(authService.register(requestDTO));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDTO> refresh(@RequestParam String refreshToken) {

        return null;
    }

}
