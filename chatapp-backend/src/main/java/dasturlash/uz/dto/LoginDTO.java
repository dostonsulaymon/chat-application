package dasturlash.uz.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank(message = "username is required")
        String login,
        @NotBlank(message = "password is required")
        String password
) {
}