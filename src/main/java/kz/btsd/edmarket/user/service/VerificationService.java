package kz.btsd.edmarket.user.service;

import kz.btsd.edmarket.common.exceptions.SmsRateLimitException;
import kz.btsd.edmarket.security.verification.model.VerificationCreatedDto;
import kz.btsd.edmarket.user.model.SmsCodeVerification;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserCheckDto;
import kz.btsd.edmarket.user.model.VerificationType;
import kz.btsd.edmarket.user.repository.SmsCodeVerificationRepository;
import kz.btsd.edmarket.user.repository.UserRepository;
import kz.btsd.edmarket.user.service.smsc.Smsc;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class VerificationService {

    private final static int SMS_RATE_LIMIT_IN_SECONDS = 60;

    private final static String APPLE_TEST_PHONE = "77010843967";
    private final static String APPLE_TEST_PHONE_2 = "+77010843967";
    private final static long APPLE_TEST_CODE = 1459;

    @Value("${jumysbar.sms.enabled}")
    boolean smsEnabled;
    @Value("${jumysbar.email.enabled}")
    private boolean emailEnabled;
    @Value("${jumysbar.sms.login}")
    private String login;
    @Value("${jumysbar.sms.password}")
    private String password;

    @Autowired
    private  SmsCodeVerificationRepository verificationRepository;
    @Autowired
    private  UserEmailSender userEmailSender;
    @Autowired
    private UserRepository userRepository;

    public void sendSms(String phone, String text) {
        if (smsEnabled) {
            Smsc smsc = new Smsc(login, password);
            String[] ret = smsc.send_sms(phone, text, 0, "", "", 0, "", "");
        }
    }

    public VerificationCreatedDto sendEmail(VerificationType type, String email) {
        User user = userRepository.findByEmailAndDeletedFalse(email).get();
        if (!checkRateLimit(user.getPhone())) {
            throw new SmsRateLimitException("Превышен лимит отправки смс");
        }
        long smsCode;
        if (emailEnabled) {
            smsCode = SmsCodeGenerator.generate();
            userEmailSender.sendEmail(user.getEmail(), smsCode);
        } else {
            smsCode = SmsCodeGenerator.mockGenerate();
        }
        SmsCodeVerification smsCodeVerification = new SmsCodeVerification()
                .setSmsCode(smsCode)
                .setPhone(user.getPhone())
                .setUserId(user.getId())
                .setType(type);

        return new VerificationCreatedDto(verificationRepository.save(smsCodeVerification).getId());
    }


    public VerificationCreatedDto sendSms(VerificationType type, String phone) {
        if (!checkRateLimit(phone)) {
            throw new SmsRateLimitException("Превышен лимит отправки смс");
        }
        long smsCode;
        if (APPLE_TEST_PHONE.equals(phone)
            || APPLE_TEST_PHONE_2.equals(phone)) {
            smsCode = APPLE_TEST_CODE;
        } else if (smsEnabled) {
            smsCode = SmsCodeGenerator.generate();
            Smsc smsc = new Smsc(login, password);
            sendSms(phone, "jumysbar.kz ваш проверочный код: " + smsCode);
        } else {
            smsCode = SmsCodeGenerator.mockGenerate();
        }

        SmsCodeVerification smsCodeVerification = new SmsCodeVerification()
                .setSmsCode(smsCode)
                .setPhone(phone)
                .setType(type);

        return new VerificationCreatedDto(verificationRepository.save(smsCodeVerification).getId());
    }

    public boolean checkRateLimit(String phone) {
        Optional<SmsCodeVerification> lastVerificationOpt = verificationRepository.findFirstByPhoneOrderByCreatedDateDesc(phone);
        if (lastVerificationOpt.isPresent()) {
            Date lastSentDate = lastVerificationOpt.get().getCreatedDate();
            Date currentDate = new Date();
            return currentDate.compareTo(DateUtils.addSeconds(lastSentDate, SMS_RATE_LIMIT_IN_SECONDS)) > 0;
        }
        return true;
    }

    public SmsCodeVerification findById(Long id) {
        return verificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not find verification by id " + id));
    }

    public boolean verify(Long verificationId, VerificationType type, Long code) {
        SmsCodeVerification verification = findById(verificationId);
        if (verification.isConfirmed()) {
            log.error("SmsCodeVerification id={} already confirmed", verification.getId());
            return false;
        }
        if (!verification.getType().equals(type)) {
            log.error("SmsCodeVerification id={} does not match with request type {}.",
                    verification.getId(),
                    type);
            return false;
        }
        if (!verification.getSmsCode().equals(code)) {
            log.error("SmsCodeVerification id={} does not match with request code {}.",
                    verification.getId(),
                    code);
            return false;
        }
        verification.setConfirmed(true);
        verificationRepository.save(verification);
        return true;
    }

    public boolean resetCheck(UserCheckDto reset) {
        boolean confirmed = false;
        SmsCodeVerification registration = verificationRepository.findById(reset.getRegistrationId())
                .orElseThrow(() -> new UsernameNotFoundException("несуществующая заявка на восстановление с registrationId=" + reset.getRegistrationId()));
        if (registration.getSmsCode().equals(reset.getSmsCode())) {
            confirmed = true;
        }
        return confirmed;
    }
}
