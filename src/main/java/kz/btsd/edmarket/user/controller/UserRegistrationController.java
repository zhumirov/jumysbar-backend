package kz.btsd.edmarket.user.controller;

import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.security.config.JwtTokenProvider;
import kz.btsd.edmarket.user.ecommerce.EcommerceService;
import kz.btsd.edmarket.user.ecommerce.UserEcommerceInfo;
import kz.btsd.edmarket.user.model.EmailVerification;
import kz.btsd.edmarket.user.model.ResetConfirmedDto;
import kz.btsd.edmarket.user.model.ResetUserCheckDto;
import kz.btsd.edmarket.user.model.SignupDto;
import kz.btsd.edmarket.user.model.SignupEmailDto;
import kz.btsd.edmarket.user.model.SignupOrgCheckDto;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserCheckDto;
import kz.btsd.edmarket.user.model.UserConverter;
import kz.btsd.edmarket.user.model.erg.SignupEmployeeDto;
import kz.btsd.edmarket.user.repository.EmailVerificationRepository;
import kz.btsd.edmarket.user.repository.UserRepository;
import kz.btsd.edmarket.user.service.UserEmailSender;
import kz.btsd.edmarket.user.service.UserService;
import kz.btsd.edmarket.user.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;


//todo продумать создание сущностей registration - их максимальное количество
@CrossOrigin(origins = "*")
@RestController
public class UserRegistrationController {
    @Autowired
    private UserService userService;
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserConverter userConverter;
    @Autowired
    private EcommerceService ecommerceService;
    @Autowired
    private UserEmailSender userEmailSender;

    @PostMapping("/users/reg")
    public SignupDto signup(@Valid @RequestBody SignupOrgCheckDto signup) {
        return userService.signup(signup);
    }

    @PostMapping("/users/reg/email")
    public ResponseEntity<?> regEmail(@Valid @RequestBody SignupEmailDto signup) {
        userService.signup(signup);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/reg/employee")
    public SignupDto regEmployeeIds(@Valid @RequestBody SignupEmployeeDto signup) {
       return userService.signup(signup);
    }


    @GetMapping("/users/{id}/send/email/confirm")
    public ResponseEntity<?> sendConfirmEmail(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        if (user.isEmailConfirmed()) {
            throw new IllegalStateException("ваша почта подтверждена");
        }
        LocalDateTime yesterdayLDT = LocalDateTime.now().minusDays(1);
        Date yesterdayDate = Date
                .from(yesterdayLDT.atZone(ZoneId.of("Asia/Almaty"))
                        .toInstant());
        Integer count = emailVerificationRepository.countForOneDay(id, yesterdayDate);
        if (count != null && count > 5) {
            throw new IllegalStateException("не более 5 писем в сутки");
        } else {
            userEmailSender.processSendConfirmEmail(user);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{id}/email/confirm/{uuid}")
    public SignupDto confirmEmail(@PathVariable Long id, @PathVariable String uuid) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        Optional<EmailVerification> emailVerificationOptional = emailVerificationRepository.findByUserIdAndUuid(id, uuid);
        if (emailVerificationOptional.isPresent()) {
            EmailVerification emailVerification = emailVerificationOptional.get();
            emailVerification.setConfirmed(true);
            emailVerificationRepository.save(emailVerification);
            user.setEmailConfirmed(true);
            user = userRepository.save(user);
            String token = jwtTokenProvider.createToken(user.getId());
            return new SignupDto(true, userConverter.convertToDto(user), user.getId(), token);
        } else {
            return new SignupDto(false);
        }
    }

    /**
     * изменение пароля
     *
     * @param reset
     * @return
     */
    @PostMapping("/users/reset/change")
    public ResetConfirmedDto changePassword(@Valid @RequestBody ResetUserCheckDto reset) {
        return userService.resetPassword(reset);
    }


    @PostMapping("/users/reset/check")
    public ResetConfirmedDto resetCheck(@Valid @RequestBody UserCheckDto reset) {
        ResetConfirmedDto resetDto = new ResetConfirmedDto(verificationService.resetCheck(reset));
        return resetDto;
    }

    @GetMapping("/users/ecommerce/check")
    public UserEcommerceInfo ecommerceCheck(@Valid @RequestParam String identifier) {
        if (identifier.length() != 12) {
            throw new IllegalArgumentException("введите 12 символов");
        }
        return ecommerceService.getResult(identifier);
    }
}
