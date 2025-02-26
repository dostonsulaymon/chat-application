package dasturlash.uz.controller;

import dasturlash.uz.dto.LoginDTO;
import dasturlash.uz.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String > login(@RequestBody LoginDTO loginDTO) {

        return null;
    }


}
