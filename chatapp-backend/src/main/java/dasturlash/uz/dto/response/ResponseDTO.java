package dasturlash.uz.dto.response;

public record ResponseDTO(
        String telegramId,
        boolean isValid,
        String firstName,
        String lastName,
        String phoneNumber
) {}
