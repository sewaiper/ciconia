package ru.sewaiper.ciconia.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sewaiper.ciconia.service.auth.TdlibAuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class TdlibAuthController {

    private final TdlibAuthService authService;

    public TdlibAuthController(TdlibAuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public String getState() {
        return authService.getState().name();
    }

    @PostMapping(value = "/phone", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void setPhone(@RequestBody String phone) {
        authService.setPhoneNumber(phone);
    }

    @PostMapping(value = "/code", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void setCode(@RequestBody String code) {
        authService.setTgCode(code);
    }
}
