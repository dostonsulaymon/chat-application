package dasturlash.uz.dto.response;

public record ResponseDTO(
        String telegramId,
        String username,
        boolean isValid,
        String firstName,
        String lastName,
        String phoneNumber
) {}
