package dasturlash.uz.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginByBotDTO(
        @NotBlank(message = "Code is required")
        String code) {


}
