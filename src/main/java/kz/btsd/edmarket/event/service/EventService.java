package kz.btsd.edmarket.event.service;

import kz.btsd.edmarket.common.controller.utils.SortUtils;
import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.elastic.EventElasticService;
import kz.btsd.edmarket.event.model.CommentRating;
import kz.btsd.edmarket.event.model.EntityStatus;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.EventConverter;
import kz.btsd.edmarket.event.model.EventResponse;
import kz.btsd.edmarket.event.model.EventStatus;
import kz.btsd.edmarket.event.model.EventTitleDto;
import kz.btsd.edmarket.event.model.Plan;
import kz.btsd.edmarket.event.moderation.EventModeration;
import kz.btsd.edmarket.event.moderation.EventModerationRepository;
import kz.btsd.edmarket.event.moderation.ModerationStatus;
import kz.btsd.edmarket.event.promocode.PromoCodeRepository;
import kz.btsd.edmarket.event.promocode.PromoStartDate;
import kz.btsd.edmarket.event.promocode.Promocode;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.event.repository.PlanRepository;
import kz.btsd.edmarket.online.model.SectionFullDto;
import kz.btsd.edmarket.online.module.model.Module;
import kz.btsd.edmarket.online.module.model.ModuleConverter;
import kz.btsd.edmarket.online.module.model.ModuleDto;
import kz.btsd.edmarket.online.module.service.ModuleService;
import kz.btsd.edmarket.review.repository.ReviewRepository;
import kz.btsd.edmarket.user.model.Platform;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserRole;
import kz.btsd.edmarket.user.repository.UserRepository;
import kz.btsd.edmarket.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventConverter eventConverter;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ModuleService moduleService;
    @Lazy
    @Autowired
    private EventElasticService eventElasticService;
    @Autowired
    private EventModerationRepository eventModerationRepository;
    @Autowired
    private ModuleConverter moduleConverter;
    @Autowired
    private UserService userService;
    @Autowired
    private PromoCodeRepository promoCodeRepository;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private UserRepository userRepository;

    public Date strToDate(LocalDate date, LocalTime time) {
        LocalDateTime dateToConvert = LocalDateTime.of(date, time);
        return Date
                .from(dateToConvert.atZone(ZoneId.of("Asia/Almaty"))
                        .toInstant());
    }

    //todo вынести и переделать на конвертацию переменной newPromocodde
    private void fillPromoCodeDate(Event event) {
        if (event.getPromoCodes() != null) {
            for (Promocode promocode :
                    event.getPromoCodes()) {
                // promocode.setEventId(event.getId());
                // promocode.setUserId(event.getUserId());
                if (promocode.getPromoStartDate().equals(PromoStartDate.PROMO)) {
                    promocode.setStartDateTime(strToDate(LocalDate.parse(promocode.getStartDate()), LocalTime.parse(promocode.getStartTime())));
                } else {
                    promocode.setStartDateTime(strToDate(LocalDate.now(), LocalTime.now()));
                }
            }
        }
    }

    public void fillEventDate(Event newEvent) {
        fillPromoCodeDate(newEvent);
        if (newEvent.getId() != null) {
            calculateAndAddOldPlans(newEvent);
            calculateAndAddOldPromocodes(newEvent);
        }
    }

    private void calculateAndAddOldPromocodes(Event newEvent) {
        Event savedEvent = eventRepository.findById(newEvent.getId()).get();
        Map<Long, Promocode> savedPromocodeMap = new HashMap<>();
        for (Promocode promocode :
                savedEvent.getPromoCodes()) {
            savedPromocodeMap.put(promocode.getId(), promocode);
        }
        if (newEvent.getPromoCodes() != null) {
            for (Promocode promocode : newEvent.getPromoCodes()) {
                if (promocode.getId() != null) {
                    savedPromocodeMap.remove(promocode.getId());
                }
            }
        }
        for (Long promocodeId : savedPromocodeMap.keySet()) {
            if (!promoCodeRepository.findByParentId(promocodeId).isPresent()) {
                promoCodeRepository.deleteById(promocodeId);
            } else {
                Promocode promocode = savedPromocodeMap.get(promocodeId);
                promocode.setStatus(EntityStatus.DELETED);
                newEvent.getPromoCodes().add(promocode);
            }
        }
    }

    private void calculateAndAddOldPlans(Event newEvent) {
        Event savedEvent = eventRepository.findById(newEvent.getId()).get();
        Map<Long, Plan> savedPlanMap = new HashMap<>();
        for (Plan plan :
                savedEvent.getPlans()) {
            savedPlanMap.put(plan.getId(), plan);
        }
        for (Plan plan : newEvent.getPlans()) {
            if (plan.getId() != null) {
                savedPlanMap.remove(plan.getId());
            }
        }
        for (Long planId : savedPlanMap.keySet()) {
            if (!planRepository.findByParentId(planId).isPresent()) {
                planRepository.deleteById(planId);
            } else {
                Plan plan = savedPlanMap.get(planId);
                plan.setStatus(EntityStatus.DELETED);
                newEvent.getPlans().add(plan);
            }
        }
    }

    public void checkEventOwner(String userId, Long eventId) {
        User owner = userService.findById(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(eventId));
        if (!(owner.getId().equals(event.getUserId()) || owner.getUserRole().equals(UserRole.ADMIN))) {
            throw new AccessDeniedException("Только создатель может публиковать");
        }
    }

    public void removeDeletes(Event event) {
        event.getPromoCodes().removeIf(promocode -> promocode.getStatus().equals(EntityStatus.DELETED));
        event.getPlans().removeIf(plan -> plan.getStatus().equals(EntityStatus.DELETED));
    }

    public void removeDeletes(List<ModuleDto> moduleDtos) {
        moduleDtos.removeIf(moduleDto -> moduleDto.getStatus().equals(EntityStatus.DELETED));
        for (ModuleDto moduleDto:
             moduleDtos) {
            moduleDto.getSections().removeIf(sectionFullDto -> sectionFullDto.getStatus().equals(EntityStatus.DELETED));
            for (SectionFullDto sectionFullDto:
                 moduleDto.getSections()) {
                sectionFullDto.getSubsections().removeIf(subsectionDto -> subsectionDto.getStatus().equals(EntityStatus.DELETED));
            }
        }
    }

    public EventResponse findById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        removeDeletes(event);
        EventResponse eventResponse = new EventResponse();
        eventResponse.setEvent(eventConverter.convertToDto(event));
        eventResponse.setCommentRating(commentRating(id));
        eventResponse.setModules(moduleService.findAllByEventId(id));
        eventResponse.setChildStatus(getChildStatus(id));
        removeDeletes(eventResponse.getModules());//todo определить когда показывать удаленные
        eventModerationRepository.findByEventId(id).ifPresent(eventResponse::setModeration);
        return eventResponse;
    }

    public Event save(Event event) {
        event = eventRepository.save(event); //todo переделать на события
        eventElasticService.publish(event);
        return event;
    }

    public Long getOwnerId(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"))
                .getUserId();
    }

    //todo перенести в comment service
    public CommentRating commentRating(Long eventId) {
        CommentRating commentRating = new CommentRating();
        commentRating.setAmount(reviewRepository.countByEventId(eventId));
        commentRating.setRating(reviewRepository.avgRating(eventId));
        return commentRating;
    }

    public Long getParentEventId(Long eventId) {
        Event event = eventRepository.findById(eventId).get();
        if (!event.isPublishVersion()) {
            return eventId;
        } else {
            return event.getParentId();
        }
    }

    public Long getChildEventId(Long eventId) {
        Event event = eventRepository.findById(eventId).get();
        if (event.isPublishVersion()) {
            return eventId;
        } else {
            return eventRepository.findByParentId(event.getId()).get().getId();
        }
    }

    public EventStatus getChildStatus(Long eventId) {
        Long childEventId=getChildEventId(eventId);
        if (childEventId != null) {
            Event childEvent = eventRepository.findById(childEventId).get();
            return childEvent.getStatus();
        } else {
            return null;
        }
    }

    public boolean checkUserModerationForUserRole(User owner, Event publishedEvent) {
        boolean publishElastic = true;
        if (owner.isUser()) {
            Optional<EventModeration> moderationOptional = eventModerationRepository.findByEventId(publishedEvent.getId());
            if (moderationOptional.isPresent()) {
                EventModeration eventModeration = moderationOptional.get();
                if (eventModeration.getStatus().equals(ModerationStatus.APPROVED)) {
                    publishElastic = true;
                } else {
                    publishElastic = false;
                    eventModeration.setStatus(ModerationStatus.NEW);
                    eventModerationRepository.save(eventModeration);
                }
            } else {
                publishElastic = false;
                EventModeration eventModeration = new EventModeration(publishedEvent.getId(), publishedEvent.getUserId());
                eventModerationRepository.save(eventModeration);
            }
        }
        return publishElastic;
    }

    /**
     * вернет id опубликованной версии курса.
     */
    public Long getPublishEventId(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Курс не найден"));
        if (!EventStatus.APPROVED.equals(event.getStatus())) {
            event = eventRepository.findByParentId(event.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Курс не опубликован"));
            return event.getId();
        }
        return eventId;
    }

    public EventTitleDto cloneWithOutId(Long id) {
        EventResponse eventResponse = findById(id);
        Event clone = eventConverter.cloneWithOutId(eventResponse.getEvent());
        clone = save(clone);
        for (ModuleDto module :
                eventResponse.getModules()) {
            Module moduleClone = moduleConverter.cloneWithEventId(module, clone.getId());
            moduleService.save(moduleClone);
        }
        return new EventTitleDto(clone.getId(), clone.getTitle());
    }

    public boolean freeEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).get();
        if (event.getPlans() == null || event.getPlans().size() == 0) {
            return true;
        }
        for (Plan plan : event.getPlans()) {
            if (plan.getPrice() == 0) {
                return true;
            }
        }
        return false;
    }

    public List<Event> findOrgEventByUserId(Long userId, Platform platform, boolean deleted, boolean otherUsers, String sort, String order) {
        List<EventStatus> statuses;
        if (deleted) {
            statuses = Arrays.asList(EventStatus.DELETED);
        } else {
            statuses = Arrays.asList(EventStatus.APPROVED, EventStatus.DRAFT);
        }
        List<Event> events;
        if (otherUsers) {
            User user = userRepository.findById(userId).get();
            if (user.isAdmin()) {
                events = eventRepository.findAllByUserIdNotAndPlatformAndStatusIn(userId, platform, statuses, SortUtils.buildSort(sort, order));
            } else {
                events = new ArrayList<>();
            }
        } else {
            events = eventRepository.findAllByUserIdAndPlatformAndStatusIn(userId, platform, statuses, SortUtils.buildSort(sort, order));
        }
        Set<Long> haveApproved = new HashSet<Long>();
        for (Event event : events) {
            if (event.isPublishVersion()) {
                haveApproved.add(event.getParentId());
            }
        }
        List<Event> oneVersions = new ArrayList<>();
        for (Event event : events) {
            //не добавляем draft у которых есть опубликованная версия
            if (!haveApproved.contains(event.getId())) {
                oneVersions.add(event);
            }
        }
        return oneVersions;
    }
}
