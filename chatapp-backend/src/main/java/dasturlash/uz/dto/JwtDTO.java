package dasturlash.uz.dto;

import java.util.List;

public record JwtDTO(
        String login,
        List<String> roles
) {

}


