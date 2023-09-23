package kz.btsd.edmarket.webinar.controller;

import kz.btsd.edmarket.webinar.service.WebinarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class WebinarController {
    @Autowired
    private WebinarService webinarService;

    @GetMapping("/webinar/join")
    public String join(Authentication authentication,
                       @RequestParam Long subsectionId,
                       @RequestParam(required = false) Long userId) {
        return webinarService.getJoinLink(subsectionId, userId);
    }

    //todo for test - delete test
    @GetMapping("/webinar/create")
    public String create(Authentication authentication,
                         @RequestParam String webinarId) {
        return webinarService.createAndOpen(webinarId);
    }

//    @GetMapping("/webinar/create-test")
//    public void create(Authentication authentication, @RequestParam Long meet) {
//        String meetingId = "random-"+meet;
//       webinarService.createAndOpen(meetingId);
//    }
//
//    @GetMapping("/webinar/join-test")
//    public String jj(Authentication authentication, @RequestParam Long meet) {
//        String meetingId = "random-"+meet;
//        return webinarService.joinMethodLink("andr", meetingId, false);
//    }
}
