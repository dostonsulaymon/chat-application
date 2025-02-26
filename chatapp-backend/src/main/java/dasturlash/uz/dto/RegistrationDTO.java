package dasturlash.uz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationDTO(
        @NotBlank(message = "Firstname is required.")
        @Size(max = 100, message = "Firstname cannot exceed 100 characters.")
        String firstName,

        @NotBlank(message = "Lastname is required.")
        @Size(max = 100, message = "Lastname cannot exceed 100 characters.")
        String lastName,

        @NotBlank(message = "Login is required.")
        String login,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters long.")
        String password
) {}