package dasturlash.uz.service;


import dasturlash.uz.dto.response.EmailMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSendingService {

    @Value("${spring.mail.username}")
    private String emailSender;


    private final JavaMailSender javaMailSender;

    public String sendSimpleMessage(EmailMessageDTO messageDTO) {


        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailSender);
        message.setTo(messageDTO.getTo());
        message.setText(messageDTO.getText());
        message.setSubject(messageDTO.getSubject());
        javaMailSender.send(message);
        return "Email sent successfully";
    }


}
