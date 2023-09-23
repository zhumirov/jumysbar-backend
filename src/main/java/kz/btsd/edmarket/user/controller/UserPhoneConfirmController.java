package kz.btsd.edmarket.user.controller;

import kz.btsd.edmarket.security.verification.model.VerificationCreatedDto;
import kz.btsd.edmarket.security.verification.model.VerificationUserPhoneDto;
import kz.btsd.edmarket.user.model.ConfirmUserPhoneDto;
import kz.btsd.edmarket.user.model.ResetConfirmedDto;
import kz.btsd.edmarket.user.model.VerificationType;
import kz.btsd.edmarket.user.repository.UserRepository;
import kz.btsd.edmarket.user.service.UserService;
import kz.btsd.edmarket.user.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@CrossOrigin(origins = "*")
@RestController
public class UserPhoneConfirmController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private VerificationService service;

    /**
     * подтверждение телефона по смс, отправка заявки.
     *
     * @param verificationDto
     * @return
     */
    @PostMapping("/users/phone/verification")
    public VerificationCreatedDto changePhone(@Valid @RequestBody VerificationUserPhoneDto verificationDto) {
        if (!userRepository.findByPhoneAndDeletedFalse(verificationDto.getPhone()).isPresent()) {
            throw new BadCredentialsException("Пользователь с номером " + verificationDto.getPhone() + " не зарегистрирован");
        }
        return service.sendSms(VerificationType.CHANGE_PHONE, verificationDto.getPhone());
    }

    /**
     * подтверждение телефона
     *
     * @param confirm
     * @return
     */
    @PostMapping("/users/phone/confirm")
    public ResetConfirmedDto confirmPhone(@Valid @RequestBody ConfirmUserPhoneDto confirm) {
        return userService.confirmPhone(confirm);
    }
}
