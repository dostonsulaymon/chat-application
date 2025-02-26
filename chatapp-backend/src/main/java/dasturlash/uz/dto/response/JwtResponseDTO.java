package dasturlash.uz.dto.response;

import dasturlash.uz.enums.UserRole;


public record JwtResponseDTO(
        String token,
        String refreshToken,
        String login,
        UserRole role
) {
}