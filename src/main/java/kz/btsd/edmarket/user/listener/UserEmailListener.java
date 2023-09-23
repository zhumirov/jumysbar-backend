package kz.btsd.edmarket.user.listener;

import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.service.UserEmailSender;
import kz.btsd.edmarket.user.service.VerificationService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class UserEmailListener {
    @Autowired
    private JavaMailSenderImpl sender;
    @Value("classpath:email/welcome.html")
    private Resource emailContent;
    @Value("${jumysbar.email.enabled}")
    private boolean emailEnabled;
    @Value("${jumysbar.bitrix.enabled}")
    private boolean bitrixEnabled;
    @Value("${jumysbar.bitrix.url}")
    private String bitrixUrl;
    @Value("${jumysbar.backend.url}")
    private String backendUrl;
    @Value("${spring.profiles}")
    private String profiles;
    @Value("${jumysbar.sms.enabled}")
    boolean smsEnabled;
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private UserEmailSender userEmailSender;
    public static final String REPLACE_SITE_URL = "&replace-site-url&";


    @Async
    @EventListener
    public void processUserCreatedEvent(UserCreatedEvent event) {
        if (bitrixEnabled) {
         //   sendBitrix(event.getUser(), event.getBitrixPage());
        }
        if (emailEnabled) {
           // sendEmail(event.getUser());
            //   if (Platform.isCorp(event.getUser().getPlatform())) {
            userEmailSender.processSendConfirmEmail(event.getUser());
            //   }
        }
        if (smsEnabled && event.getPassword() != null && StringUtils.isNotBlank(event.getUser().getPhone())) {
            String text = "Спасибо что вы зарегистрировались. логин: " + event.getUser().getPhone() + " пароль: " + event.getPassword();
            verificationService.sendSms(event.getUser().getPhone(), text);
        }
    }


    private void sendBitrix(User user, String bitrixPage) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("FIELDS[TITLE]", "JUMYSBAR-TEST-DEV Заявка от " + user.getName() + " Курс: " + bitrixPage);
        map.add("FIELDS[NAME]", user.getName());
        map.add("FIELDS[EMAIL][0][VALUE]", user.getEmail());
        map.add("FIELDS[PHONE][0][VALUE]", user.getPhone());
        map.add("FIELDS[PAGE]", bitrixPage);


        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> response =
                restTemplate.exchange(bitrixUrl,
                        HttpMethod.POST,
                        entity,
                        String.class);
    }

    private String getHtml() {
        String html = "";
        try {
            html = IOUtils.toString(emailContent.getInputStream(), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html;
    }

    public void sendEmail(User user) {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getEmail());
            helper.setSubject("Добро пожаловать в Jumysbar.kz");
            helper.setText(getHtml(), true);
            sender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
