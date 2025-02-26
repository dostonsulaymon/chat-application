package dasturlash.uz.dto.response;

// keyinchalik tokenni o'rniga jwt response dto ni jo'natamiz
public record LoginResponseDTO(String message, JwtResponseDTO token) {}
