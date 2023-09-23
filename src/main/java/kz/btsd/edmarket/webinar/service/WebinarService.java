package kz.btsd.edmarket.webinar.service;

import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.online.model.Subsection;
import kz.btsd.edmarket.online.model.Unit;
import kz.btsd.edmarket.online.model.UnitType;
import kz.btsd.edmarket.online.repository.SubsectionRepository;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class WebinarService {
    private RestTemplate restTemplate;

    @Value("${jumysbar.bbb.url}")
    private String url;
    @Value("${jumysbar.bbb.moderator-password}")
    private String mp;
    @Value("${jumysbar.bbb.attendee-password}")
    private String ap;
    @Value("${jumysbar.bbb.shared-secret}")
    private String secret;
    @Autowired
    private SubsectionRepository subsectionRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        restTemplate = builder
                .build();
    }

    public String getJoinLink(Long subsectionId, Long userId) {
        Subsection subsection = subsectionRepository.findById(subsectionId).get();
        Event event = eventRepository.findByStepId(subsectionId);
        String username = "guest";
        if (userId != null) {
            User user = userRepository.findById(userId).get();
            username = user.getName();
        }
        boolean moderator = event.getUserId().equals(userId);
        String meetingId = "";
        for (Unit unit : subsection.getUnits()) {
            if (unit.getType().equals(UnitType.WEBINAR)) {
                meetingId = unit.getValue();
                break;
            }
        }
        return joinMethodLink(username, meetingId, moderator);
    }

    public String sha1(String input) {
        MessageDigest mDigest = null;
        try {
            mDigest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    @Async
    public String createAndOpen(String meetingId) {
        String url = createMethodLink(meetingId);
        return restTemplate.getForObject(url, String.class);
    }

    public String createMethodLink(String meetingId) {
        long durationMonth = 43800;
        long meetingExpireIfNoUserJoinedInMinutes = 43800;
        long meetingExpireWhenLastUserLeftInMinutes = 43800;
        String methodName = "create";
        String mainBody = "allowStartStopRecording=true";
        mainBody += "&attendeePW=" + ap;
        mainBody += "&meetingID=" + meetingId;
        mainBody += "&moderatorPW=" + mp;
        mainBody += "&name=" + meetingId;
        mainBody += "&record=true";
        mainBody += "&duration="+durationMonth;
        mainBody += "&meetingExpireIfNoUserJoinedInMinutes="+meetingExpireIfNoUserJoinedInMinutes;
        mainBody += "&meetingExpireWhenLastUserLeftInMinutes="+meetingExpireWhenLastUserLeftInMinutes;
        //mainBody += "&voiceBridge="+voiceBridge;
        String forSha1 = methodName + mainBody + secret;
        String checksum = sha1(forSha1);
        String result = url + "/" + methodName + "?" + mainBody + "&checksum=" + checksum;
        return result;
    }

    public String joinMethodLink(String username, String meetingId, boolean moderator) {
        String password = moderator ? mp : ap;
        String methodName = "join";
        String mainBody = "fullName=" + username.replace(" ", "+");
        mainBody += "&" + "meetingID=" + meetingId;
        mainBody += "&" + "password=" + password;
        mainBody += "&" + "redirect=true";
        String forSha1 = methodName + mainBody + secret;
        String checksum = sha1(forSha1);
        String result = url + "/" + methodName + "?" + mainBody + "&checksum=" + checksum;
        return result;
    }
}

