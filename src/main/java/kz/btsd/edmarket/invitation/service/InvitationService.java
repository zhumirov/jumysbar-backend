package kz.btsd.edmarket.invitation.service;

import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.EventStatus;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.invitation.model.InvitationDto;
import kz.btsd.edmarket.invitation.model.InvitationEntity;
import kz.btsd.edmarket.invitation.model.InvitationListDto;
import kz.btsd.edmarket.invitation.repository.InvitationRepository;
import kz.btsd.edmarket.subscription.repository.SubscriptionRepository;
import kz.btsd.edmarket.subscription.service.SubscriptionService;
import kz.btsd.edmarket.user.listener.UserCreatedEvent;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserDto;
import kz.btsd.edmarket.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;

@AllArgsConstructor
@Service
public class InvitationService {
    private final EventRepository eventRepository;
    private final InvitationRepository invitationRepository;
    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;
    private final EventService eventService;
    private final ApplicationEventPublisher publisher;

    public boolean inviteExist(InvitationEntity invitationEntity) {
        if (isNoneBlank(invitationEntity.getPhone())
                && invitationRepository.existsByPhoneAndEventId(invitationEntity.getPhone(), invitationEntity.getEventId())) {
            return true;
        }
        if (isNoneBlank(invitationEntity.getEmail())
                && invitationRepository.existsByEmailAndEventId(invitationEntity.getEmail(), invitationEntity.getEventId())) {
            return true;
        }
        return false;
    }

    public void completeDeletePhoneInvite(String phone, Long eventId) {
        if (userService.existsByPhone(phone)) {
            UserDto user = userService.findByPhoneToDto(phone);
            subscriptionRepository.deleteByUserIdAndEventId(user.getId(), eventId);
        }
        invitationRepository.deleteByPhoneAndEventId(phone, eventId);
    }

    public void completeDeleteEmailInvite(String email, Long eventId) {
        if (userService.existsByEmail(email)) {
            UserDto user = userService.findByEmail(email);
            subscriptionRepository.deleteByUserIdAndEventId(user.getId(), eventId);
        }
        invitationRepository.deleteByEmailAndEventId(email, eventId);
    }

    private void inviteToEvent(InvitationEntity invitationEntity) {
        if (inviteExist(invitationEntity)) {
            return;
        }
        completeInvite(invitationRepository.save(invitationEntity));
    }

    public void checkAndSubscribe(Long eventId, Long userId) {
        if (!subscriptionRepository.existsByEventIdAndUserId(eventId, userId)) {
            subscriptionService.createAndSaveSubscription(eventId, userId, 0L, null);
        }
    }

    public void completeInvite(InvitationEntity invitationEntity) {
        UserDto user = null;

        if (isNoneBlank(invitationEntity.getPhone()) && userService.existsByPhone(invitationEntity.getPhone())) {
            user = userService.findByPhoneToDto(invitationEntity.getPhone());
        }
        if (isNoneBlank(invitationEntity.getEmail()) && userService.existsByEmail(invitationEntity.getEmail())) {
            user = userService.findByEmail(invitationEntity.getEmail());
        }
        if (user != null) {
            //todo бесплатный инвайт?
            checkAndSubscribe(invitationEntity.getEventId(), user.getId());
        }
    }

    public void completeInvites(String phone) {
        invitationRepository.findAllByPhone(phone)
                .forEach(this::completeInvite);
    }

    @Async
    @EventListener
    public void processUserCreatedEvent(UserCreatedEvent event) {
        completeInvites(event.getUser().getPhone());
    }

    public void inviteToEvent(InvitationListDto invitationListDto) {
        invitationListDto.setEventId(eventService.getPublishEventId(invitationListDto.getEventId()));
        invitationListDto.getPhones()
                .forEach(phone -> {
                    InvitationEntity invitationEntity = new InvitationEntity(phone, invitationListDto.getEventId());
                    inviteToEvent(invitationEntity);
                });
        invitationListDto.getEmails()
                .forEach(email -> {
                    InvitationEntity invitationEntity = new InvitationEntity(invitationListDto.getEventId(), email);
                    inviteToEvent(invitationEntity);
                    publisher.publishEvent(new InvitationEvent(this, invitationEntity));
                });
    }

    public void deleteToEvent(User user, Long eventId) {
        if (user.getEmail() != null) {
            completeDeleteEmailInvite(user.getEmail(), eventId);
        }
        if (user.getPhone() != null) {
            completeDeletePhoneInvite(user.getPhone(), eventId);
        }
    }

    public void deleteToEvent(InvitationDto invitationDto) {
        invitationDto.setEventId(eventService.getPublishEventId(invitationDto.getEventId()));
        completeDeletePhoneInvite(invitationDto.getValue(), invitationDto.getEventId());
        completeDeleteEmailInvite(invitationDto.getValue(), invitationDto.getEventId());
    }

    public List<InvitationEntity> findAllByEventId(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        if (!EventStatus.APPROVED.equals(event.getStatus())) {
            Optional<Event> eventOptional = eventRepository.findByParentId(event.getId());
            if (eventOptional.isPresent()) {
                event = eventOptional.get();
            } else {
                return new ArrayList<>();
            }
        }
        return invitationRepository.findAllByEventId(event.getId());
    }

}
