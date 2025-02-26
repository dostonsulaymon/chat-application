package dasturlash.uz.dto;

import dasturlash.uz.enums.UserRole;

import java.util.List;

public record JwtDTO(
        String login,
        UserRole role
) {

}


