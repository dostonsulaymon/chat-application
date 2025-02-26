package dasturlash.uz.dto;

import java.util.List;

public record JwtResponseDTO(
        String token,
        String refreshToken,
        String login,
        List<String> roles
) {
}