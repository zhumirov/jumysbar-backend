package kz.btsd.edmarket.invitation.controller;

import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.invitation.model.InvitationDto;
import kz.btsd.edmarket.invitation.model.InvitationEntity;
import kz.btsd.edmarket.invitation.model.InvitationListDto;
import kz.btsd.edmarket.invitation.service.InvitationService;
import kz.btsd.edmarket.user.model.UserDto;
import kz.btsd.edmarket.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class InvitationController {

    private final EventService eventService;
    private final InvitationService invitationService;
    private final UserService userService;

    public InvitationController(EventService eventService,
                                InvitationService invitationService,
                                UserService userService) {
        this.eventService = eventService;
        this.invitationService = invitationService;
        this.userService = userService;
    }

    @PostMapping("/invitation/list")
    public void sendCourseInvitation(@AuthenticationPrincipal Jwt jwt,
                                     @Valid @RequestBody InvitationListDto invitationListDto) {
        UserDto user = userService.findByIdtoDto(jwt.getSubject());
        if (!user.getId().equals(eventService.getOwnerId(invitationListDto.getEventId()))) {
            throw new AccessDeniedException("Access denied");
        }
        invitationService.inviteToEvent(invitationListDto);
    }

    @DeleteMapping("/invitation")
    public ResponseEntity<?> deleteCourseInvitation(@AuthenticationPrincipal Jwt jwt,
                                                    @RequestBody InvitationDto invitationDto) {
        UserDto user = userService.findByIdtoDto(jwt.getSubject());
        if (!user.getId().equals(eventService.getOwnerId(invitationDto.getEventId()))) {
            throw new AccessDeniedException("Access denied");
        }
        invitationService.deleteToEvent(invitationDto);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/invitation/invited-users")
    public List<InvitationEntity> findAll(@AuthenticationPrincipal Jwt jwt,
                                          @RequestParam("eventId") Long eventId) {
        UserDto user = userService.findByIdtoDto(jwt.getSubject());
        if (!user.getId().equals(eventService.getOwnerId(eventId))) {
            throw new AccessDeniedException("Access denied");
        }
        return invitationService.findAllByEventId(eventId);
    }
}
