package kz.btsd.edmarket.user.controller;

import kz.btsd.edmarket.user.model.Platform;
import kz.btsd.edmarket.user.model.UserTokenDto;
import kz.btsd.edmarket.user.service.UserAituService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class UserAituController {
    @Autowired
    private UserAituService userAituService;

    @GetMapping("/users/token/aitu")
    public UserTokenDto getToken(@RequestParam String code,
                                 @RequestParam(defaultValue = "JUMYSBAR", required = false) Platform platform) {
        return userAituService.loginAituByCode(code, platform);
    }

}
