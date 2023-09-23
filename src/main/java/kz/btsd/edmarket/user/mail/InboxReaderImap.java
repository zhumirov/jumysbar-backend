package kz.btsd.edmarket.user.mail;

import kz.btsd.edmarket.webkassa.model.CheckInfo;
import kz.btsd.edmarket.webkassa.model.CheckKassa;
import kz.btsd.edmarket.webkassa.model.check.WebKassaCheckResponse;
import kz.btsd.edmarket.webkassa.service.CheckKassaRepository;
import kz.btsd.edmarket.webkassa.service.WebKassaService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class InboxReaderImap {
    @Value("${spring.mail.username}")
    String username;
    @Value("${spring.mail.password}")
    String password;
    //@Value("${spring.mail.host}")
    String server = "pop.gmail.com";
    //@Value("${spring.mail.port}")
    String port = "995";

    Properties properties = new Properties();
    Folder sentFolder;
    Store store;
    @Autowired
    private WebKassaService webKassaService;
    @Autowired
    private CheckKassaRepository checkKassaRepository;

    String part1 = "Услуга:";
    String part2 = "ФИО учащегося:";
    String part3 = "ФИО плательщика:";
    String part4 = "ИИН плательщика:";
    String part5 = "Платеж на сумму:";
    String part6 = "Дата:";
    String part7 = "Идентификатор платежа:";
    String part8 = "Параметры:";
    String part9 = "Телефон нөмірі|Номер телефона =";

   // @Scheduled(fixedDelay = 60000)
    public void idle() throws Exception {
        try {
            read();
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

  //  @PostConstruct
    void setup() throws MessagingException {
        properties.put("mail.pop3.host", server);
        properties.put("mail.pop3.port", port);
        properties.put("mail.pop3.starttls.enable", true);
        Session emailSession = Session.getDefaultInstance(properties);
        Store store = emailSession.getStore("pop3s");
        store.connect(server, username, password);
        sentFolder = store.getFolder("INBOX");
        sentFolder.open(Folder.READ_ONLY);
        System.out.println("Inbox Type: ${sentFolder.getType()}");
        System.out.println("Folders: ${store.getDefaultFolder().list('*')}");
        sentFolder.close(false);
    }

    private String getPositionName(String str) {
        int start = str.indexOf(part1) + part1.length();
        int end = str.indexOf(part2);
        return StringUtils.trim(str.substring(start, end));
    }

    private String getPrice(String str) {
        int start = str.indexOf(part5) + part5.length();
        int end = str.indexOf(part6);
        return StringUtils.trim(str.substring(start, end));
    }

    private String getExternalCheckNumber(String str) {
        int start = str.indexOf(part7) + part7.length();
        int end = str.indexOf(part8);
        return StringUtils.trim(str.substring(start, end));
    }

    private String getCustomerPhone(String str) {
        int start = str.indexOf(part9) + part9.length();
        //int end = str.indexOf(part2);
        return StringUtils.trim(str.substring(start, start + 12));
    }

    public CheckInfo parse(Message message) throws Exception {
        //
        MimeMessageParser parser = new MimeMessageParser((MimeMessage) message);
        parser.parse();
        String plainContent = parser.getPlainContent();
        //

        if (plainContent.contains("ФИО учащегося:") && plainContent.contains("Идентификатор платежа:")) {
            CheckInfo checkInfo = new CheckInfo();
            checkInfo.setCustomerPhone(getCustomerPhone(plainContent));
            checkInfo.setPositionName(getPositionName(plainContent));
            checkInfo.setExternalCheckNumber(getExternalCheckNumber(plainContent));
            checkInfo.setPrice(getPrice(plainContent));
            return checkInfo;
        }
        return null;
    }

    public void read() throws Exception {
        sentFolder.open(Folder.READ_ONLY);
        int max = sentFolder.getMessageCount();
        Message[] messages = sentFolder.getMessages(max - 10, max);
        System.out.println("messages.length---" + messages.length);
        List<CheckInfo> checks = new ArrayList<>();

        for (int i = 0; i < messages.length; i++) {
            Message message = messages[i];
            CheckInfo checkInfo = parse(message);
            if (checkInfo != null) {
                checks.add(checkInfo);
            }
        }
        sentFolder.close(false);
        for (CheckInfo checkInfo : checks) {
            if (!checkKassaRepository.findByCheckNumber(checkInfo.getExternalCheckNumber()).isPresent()) {
                WebKassaCheckResponse webKassaCheckResponse =webKassaService.checkWithRepeat(checkInfo);
                boolean published = ((webKassaCheckResponse != null) && (webKassaCheckResponse.getErrors()==null));
                checkKassaRepository.save(new CheckKassa(checkInfo.getExternalCheckNumber(), published));
            }
        }
    }
}
