package kz.btsd.edmarket.user.controller;

import kz.btsd.edmarket.security.verification.model.VerificationCreatedDto;
import kz.btsd.edmarket.security.verification.model.VerificationUserDto;
import kz.btsd.edmarket.subscription.repository.SubscriptionRepository;
import kz.btsd.edmarket.user.model.VerificationType;
import kz.btsd.edmarket.user.repository.UserRepository;
import kz.btsd.edmarket.user.service.VerificationService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


//todo пара eventId+phoneId-должна быть уникальной
@CrossOrigin(origins = "*")
@RestController
public class VerificationController {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final VerificationService service;

    public VerificationController(UserRepository userRepository,
                                  SubscriptionRepository subscriptionRepository,
                                  VerificationService service) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.service = service;
    }

    /**
     * отправка подтверждения по смс для регистрации пользователя.
     *
     * @param verificationDto
     * @return
     */
    @PostMapping("/users/reg/verification")
    public VerificationCreatedDto produce(@Valid @RequestBody VerificationUserDto verificationDto) {
        if (userRepository.findByPhoneAndDeletedFalse(verificationDto.getPhone()).isPresent()) {
            throw new BadCredentialsException("Пользователь с номером " + verificationDto.getPhone() + " уже зарегистрирован");
        }
        return service.sendSms(VerificationType.REGISTRATION, verificationDto.getPhone());
    }

    /**
     * восстановление пароля пользователя по смс.
     *
     * @param verificationDto
     * @return
     */
    public VerificationCreatedDto resetPasswordPhone(VerificationUserDto verificationDto) {
        if (!userRepository.findByPhoneAndDeletedFalse(verificationDto.getPhone()).isPresent()) {
            throw new BadCredentialsException("Пользователь с номером " + verificationDto.getPhone() + " не зарегистрирован");
        }
        return service.sendSms(VerificationType.RESET_PASSWORD, verificationDto.getPhone());
    }

    /**
     * восстановление пароля пользователя по почте.
     *
     * @param verificationDto
     * @return
     */
    public VerificationCreatedDto resetPasswordEmail(VerificationUserDto verificationDto) {
        if (!userRepository.findByEmailAndDeletedFalse(verificationDto.getEmail()).isPresent()) {
            throw new BadCredentialsException("Пользователь с почтой " + verificationDto.getEmail() + " не зарегистрирован");
        }
        return service.sendEmail(VerificationType.RESET_PASSWORD, verificationDto.getEmail());
    }

    /**
     * восстановление пароля пользователя по смс.
     * восстановление пароля пользователя по почте.
     *
     * @param verificationDto
     * @return
     */
    @PostMapping("/users/reset/verification")
    public VerificationCreatedDto resetPassword(@Valid @RequestBody VerificationUserDto verificationDto) {
        if (verificationDto.getEmail() != null) {
            return resetPasswordEmail(verificationDto);
        } else {
            return resetPasswordPhone(verificationDto);
        }
    }
}
