package kz.btsd.edmarket.user.controller;

import kz.btsd.edmarket.common.controller.utils.SortUtils;
import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.event.model.EventTitleDto;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.invitation.service.InvitationService;
import kz.btsd.edmarket.user.model.link.CreateInviteLinkRequest;
import kz.btsd.edmarket.user.model.link.DeleteInviteLinkRequest;
import kz.btsd.edmarket.user.model.link.InviteLink;
import kz.btsd.edmarket.user.model.link.InviteLinkResponse;
import kz.btsd.edmarket.user.model.link.InviteStatus;
import kz.btsd.edmarket.user.model.link.InviteType;
import kz.btsd.edmarket.user.model.link.SubscribeInviteLinkRequest;
import kz.btsd.edmarket.user.repository.InviteLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
public class InviteLinkController {
    @Autowired
    private InviteLinkRepository inviteLinkRepository;
    @Autowired
    private EventService eventService;
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private EventRepository eventRepository;

    /**
     * Список активных пригласительных ссылок
     */
    @GetMapping("/invite-links/{uuid}")
    public InviteLinkResponse findByUuid(@PathVariable String uuid) {
        InviteLink inviteLink  = inviteLinkRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException(uuid));
        EventTitleDto eventTitleDto = eventRepository.findByIdEventTitleDto(inviteLink.getEventId()).get();
        return new InviteLinkResponse(inviteLink, eventTitleDto);
    }
    /**
     * Список активных пригласительных ссылок
     */
    @GetMapping("/invite-links")
    public List<InviteLink> allByEventId(@RequestParam Long eventId,
                                         @RequestParam(defaultValue = "0", required = false) Integer from,
                                         @RequestParam(defaultValue = "20", required = false) Integer size,
                                         @RequestParam(defaultValue = "createdDate", required = false) String sort,
                                         @RequestParam(defaultValue = "asc", required = false) String order) {
        Long publishEventId = eventService.getPublishEventId(eventId);
        return inviteLinkRepository.findByEventIdAndStatus(publishEventId, InviteStatus.NEW, PageRequest.of(from, size, SortUtils.buildSort(sort, order)));
    }

    /**
     * Создание пригласительной-ссылки
     */
    @PostMapping("/invite-links")
    public InviteLink createLink(Authentication authentication, @Valid @RequestBody CreateInviteLinkRequest request) {
        eventService.checkEventOwner(authentication.getName(), request.getEventId());
        String uuid = UUID.randomUUID().toString();
        InviteLink inviteLink = new InviteLink(eventService.getPublishEventId(request.getEventId()), request.getType(), uuid);
        inviteLink = inviteLinkRepository.save(inviteLink);
        return inviteLink;
    }

    /**
     * Подписание по пригласительной-ссылки
     */
    @PostMapping("/invite-links/subscribe")
    public ResponseEntity<?> subscribeWithLink(@Valid @RequestBody SubscribeInviteLinkRequest request) {
        Optional<InviteLink> inviteLinkOptional = inviteLinkRepository.findByUuid(request.getUuid());
        if (inviteLinkOptional.isPresent()) {
            InviteLink inviteLink = inviteLinkOptional.get();
            if (inviteLink.getStatus().equals(InviteStatus.CLOSED)) {
                throw new IllegalStateException("пригласительная ссылка уже использовалась");
            }
            if (inviteLink.getType().equals(InviteType.ONE)) {
                inviteLink.setStatus(InviteStatus.CLOSED);
                inviteLinkRepository.save(inviteLink);
            }
            invitationService.checkAndSubscribe(inviteLink.getEventId(), request.getUserId());
        }
        return ResponseEntity.ok().build();
    }

    /**
     * удаление пригласительной-ссылки
     */
    @DeleteMapping("/invite-links")
    public ResponseEntity<?> createLink(Authentication authentication, @RequestBody DeleteInviteLinkRequest request) {
        Optional<InviteLink> inviteLinkOptional = inviteLinkRepository.findByUuid(request.getUuid());
        if (inviteLinkOptional.isPresent()) {
            InviteLink inviteLink = inviteLinkOptional.get();
            eventService.checkEventOwner(authentication.getName(), inviteLink.getEventId());
            inviteLink.setStatus(InviteStatus.CLOSED);
            inviteLinkRepository.save(inviteLink);
        }
        return ResponseEntity.ok().build();
    }
}
