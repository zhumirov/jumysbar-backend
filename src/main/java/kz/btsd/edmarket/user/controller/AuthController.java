package kz.btsd.edmarket.user.controller;

import kz.btsd.edmarket.security.apple.AppleAuthorizationService;
import kz.btsd.edmarket.security.verification.model.VerificationCreatedDto;
import kz.btsd.edmarket.security.verification.model.VerificationUserDto;
import kz.btsd.edmarket.user.model.AppleAuthRequest;
import kz.btsd.edmarket.user.model.AuthTokenDto;
import kz.btsd.edmarket.user.model.LoginByPasswordRequest;
import kz.btsd.edmarket.user.model.LoginBySmsCodeRequest;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.VerificationType;
import kz.btsd.edmarket.user.repository.UserRepository;
import kz.btsd.edmarket.user.service.AuthService;
import kz.btsd.edmarket.user.service.UserService;
import kz.btsd.edmarket.user.service.VerificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/auth/")
@RestController
public class AuthController {

    private final AuthService authService;
    private final AppleAuthorizationService appleAuthorizationService;
    private final VerificationService verificationService;
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public AuthTokenDto login(@RequestBody LoginByPasswordRequest request) {
        return authService.loginByPassword(request.getPhone(), request.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        User user = userService.findById(authentication.getName());
        if (user.getAituTokenId() != null) {
            user.setAituTokenId(null);
            userRepository.save(user);
        }
        // TODO implement logout!!!
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login-by-sms")
    public AuthTokenDto login(@RequestBody LoginBySmsCodeRequest request) {
        return authService.loginBySmsCode(request);
    }

    @PostMapping("/request-sms")
    public VerificationCreatedDto requestSms(@RequestBody VerificationUserDto request) {
        if (userService.existsByPhone(request.getPhone())) {
            return verificationService.sendSms(VerificationType.AUTH, request.getPhone());
        } else {
            return verificationService.sendSms(VerificationType.REGISTRATION, request.getPhone());
        }
    }

    @PostMapping("/apple")
    public AuthTokenDto loginByApple(@RequestBody AppleAuthRequest appleAuthRequest) {
        return appleAuthorizationService.getToken(appleAuthRequest);
    }

}
