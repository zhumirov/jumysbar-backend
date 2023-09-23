package kz.btsd.edmarket.mentor.controller;

import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.mentor.model.MentorConverter;
import kz.btsd.edmarket.mentor.model.MentorInviteDto;
import kz.btsd.edmarket.mentor.service.MentorService;
import kz.btsd.edmarket.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class MentorController {
    @Autowired
    private MentorService mentorService;
    @Autowired
    private MentorConverter mentorConverter;
    @Autowired
    private EventService eventService;

    @PostMapping("/mentors")
    public ResponseEntity<?> save(Authentication authentication, @RequestBody MentorInviteDto mentorInviteDto) {
        eventService.checkEventOwner(authentication.getName(), mentorInviteDto.getEventId());
        mentorService.saveWithParent(mentorConverter.convertToEntity(mentorInviteDto));
        return ResponseEntity.ok().build();
    }
}
