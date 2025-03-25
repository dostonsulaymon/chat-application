package dasturlash.uz.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailMessageDTO {

    private String to;
    private String subject;
    private String text;

}
