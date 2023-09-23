package kz.btsd.edmarket.event.service;

import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.elastic.EventElasticService;
import kz.btsd.edmarket.event.model.EntityStatus;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.EventConverter;
import kz.btsd.edmarket.event.model.EventStatus;
import kz.btsd.edmarket.event.model.EventStatusResponse;
import kz.btsd.edmarket.event.model.Plan;
import kz.btsd.edmarket.event.moderation.EventModeration;
import kz.btsd.edmarket.event.moderation.EventModerationRepository;
import kz.btsd.edmarket.event.moderation.ModerationStatus;
import kz.btsd.edmarket.event.promocode.PromoCodeRepository;
import kz.btsd.edmarket.event.promocode.Promocode;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.event.repository.PlanRepository;
import kz.btsd.edmarket.mentor.model.Mentor;
import kz.btsd.edmarket.mentor.repository.MentorRepository;
import kz.btsd.edmarket.notification.service.LessonAddedEvent;
import kz.btsd.edmarket.online.model.SectionFullDto;
import kz.btsd.edmarket.online.model.SubsectionDto;
import kz.btsd.edmarket.online.module.model.ModuleDto;
import kz.btsd.edmarket.online.module.service.ModuleService;
import kz.btsd.edmarket.subscription.repository.OrderRepository;
import kz.btsd.edmarket.subscription.repository.SubscriptionRepository;
import kz.btsd.edmarket.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventPublishService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventConverter eventConverter;
    @Autowired
    private EventModerationRepository eventModerationRepository;
    @Autowired
    private EventElasticService eventElasticService;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private PromoCodeRepository promoCodeRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private MentorRepository mentorRepository;

    private void fillMentors(Long eventId, Long parentEventId) {
        List<Mentor> parentMentors = mentorRepository.findByEventId(parentEventId);
        Set<String> parentMentorIds = parentMentors
                .stream()
                .map(Mentor::getPhone)
                .collect(Collectors.toSet());
        List<Mentor> mentors = mentorRepository.findByEventId(eventId);
        Set<String> mentorIds = mentors
                .stream()
                .map(Mentor::getPhone)
                .collect(Collectors.toSet());
        parentMentorIds.removeAll(mentorIds);
        for (Mentor mentor :
                parentMentors) {
            if (parentMentorIds.contains(mentor.getPhone())) {
                mentorRepository.save(new Mentor(mentor.getPhone(), eventId, mentor.getUserId()));
            }
        }
    }

    private void deleteOldModules(List<ModuleDto> modules, List<ModuleDto> publishedModules) {
        Set<Long> moduleSet = new HashSet<>();
        for (ModuleDto moduleDto : modules) {
            moduleSet.add(moduleDto.getId());
        }
        for (ModuleDto publishedModule :
                publishedModules) {
            if (!moduleSet.contains(publishedModule.getParentId())) {
                moduleService.delete(publishedModule.getId());
            }
        }
    }

    private void deleteOldPlans(Set<Plan> plans, Set<Plan> publishedPlans) {
        Map<Long, Plan> childPlanMap = new HashMap<>();
        for (Plan plan :
                publishedPlans) {
            childPlanMap.put(plan.getParentId(), plan);
        }
        for (Plan plan :
                plans) {
            if (plan.getStatus().equals(EntityStatus.DELETED)) {
                Plan publishedPlan = childPlanMap.get(plan.getId());
                if (subscriptionRepository.countByPlanId(publishedPlan.getId()) == 0
                        && orderRepository.countByPlanId(publishedPlan.getId()) == 0) {
                    planRepository.deleteById(publishedPlan.getId());
                    planRepository.deleteById(plan.getId());
                }
            }
        }
    }

    private void deleteOldPromocodes(Set<Promocode> promocodes, Set<Promocode> publishedPromocodes) {
        Map<Long, Promocode> childPromocodeMap = new HashMap<>();
        for (Promocode promocode :
                publishedPromocodes) {
            childPromocodeMap.put(promocode.getParentId(), promocode);
        }
        for (Promocode promocode :
                promocodes) {
            if (promocode.getStatus().equals(EntityStatus.DELETED)) {
                Promocode publishedPromocode = childPromocodeMap.get(promocode.getId());
                if (subscriptionRepository.countByPromocodeId(publishedPromocode.getId()) == 0
                        && orderRepository.countByPromocodeId(publishedPromocode.getId()) == 0) {
                    promoCodeRepository.deleteById(publishedPromocode.getId());
                    promoCodeRepository.deleteById(promocode.getId());
                }
            }
        }
    }

    private Map<Long, Long>  fillPublishedPlans(Set<Plan> plans) {
        Map<Long, Long> planMap = new HashMap<>();
        for (Plan publishedPlan :
                plans) {
            planMap.put(publishedPlan.getParentId(), publishedPlan.getId());
        }
        return planMap;
    }

    private Map<Long, Long>  fillPublishedPromocodes(Set<Promocode> promocodes) {
        Map<Long, Long> promocodeMap = new HashMap<>();
        for (Promocode publishedPromocode :
                promocodes) {
            promocodeMap.put(publishedPromocode.getParentId(), publishedPromocode.getId());
        }
        return promocodeMap;
    }

    public Event publish(Long id) {
        boolean haveNewLessonsNotification = false;
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        List<ModuleDto> modules = moduleService.findAllByEventId(id);
        Optional<Event> optionalPublishedEvent = eventRepository.findByParentId(id);
        Long publishedEventId = null;
        Map<Long, Long> moduleMap = new HashMap<>();
        Map<Long, Long> sectionMap = new HashMap<>();
        Map<Long, Long> subsectionMap = new HashMap<>();
        Map<Long, Long> planMap = new HashMap<>();
        Map<Long, Long> promocodeMap = new HashMap<>();
        if (optionalPublishedEvent.isPresent()) {
            Event publishedEvent = optionalPublishedEvent.get();
            publishedEventId = publishedEvent.getId();
            deleteOldPlans(event.getPlans(), publishedEvent.getPlans());
            deleteOldPromocodes(event.getPromoCodes(), publishedEvent.getPromoCodes());
            publishedEvent = eventRepository.findByParentId(id).get();
            event = eventRepository.findById(id).get();
            planMap = fillPublishedPlans(publishedEvent.getPlans());
            promocodeMap = fillPublishedPromocodes(publishedEvent.getPromoCodes());
            List<ModuleDto> publishedModules = moduleService.findAllByEventId(publishedEventId);
            for (ModuleDto publishedModuleDto : publishedModules) {
                moduleMap.put(publishedModuleDto.getParentId(), publishedModuleDto.getId());
                for (SectionFullDto section : publishedModuleDto.getSections()) {
                    sectionMap.put(section.getParentId(), section.getId());
                    for (SubsectionDto subsection :
                            section.getSubsections()) {
                        subsectionMap.put(subsection.getParentId(), subsection.getId());
                    }
                }
            }
            deleteOldModules(modules, publishedModules);
        }
        event.setPlans(new HashSet<>(event.getPlans())); //todo обнуление, против Don't change the reference to a collection with delete-orphan enabled :
        for (Plan plan :
                event.getPlans()) {
            plan.setParentId(plan.getId());
            plan.setId(planMap.get(plan.getId()));
            plan.setEventId(publishedEventId);
        }
        event.setPromoCodes(new HashSet<>(event.getPromoCodes())); //todo обнуление, против Don't change the reference to a collection with delete-orphan enabled :
        for (Promocode promocode :
                event.getPromoCodes()) {
            promocode.setParentId(promocode.getId());
            promocode.setId(promocodeMap.get(promocode.getId()));
            promocode.setEventId(publishedEventId);
        }
        event.setParentId(event.getId());
        event.setId(publishedEventId);
        event.setStatus(EventStatus.APPROVED);
        event = eventRepository.save(event);
        fillMentors(event.getId(), event.getParentId());
        for (ModuleDto moduleDto : modules) {
            moduleDto.setParentId(moduleDto.getId());
            moduleDto.setId(moduleMap.get(moduleDto.getId()));
            moduleDto.setEventId(event.getId());
            for (SectionFullDto section : moduleDto.getSections()) {
                if (sectionMap.get(section.getId()) == null) { //для уведомлений
                    haveNewLessonsNotification = true;
                }
                section.setParentId(section.getId());
                section.setId(sectionMap.get(section.getId()));
                section.setModuleId(moduleDto.getId());
                for (SubsectionDto subsection :
                        section.getSubsections()) {
                    subsection.setParentId(subsection.getId());
                    subsection.setId(subsectionMap.get(subsection.getId()));
                    subsection.setSectionId(section.getId());
                }
            }
            moduleService.save(moduleDto);
        }
        if (haveNewLessonsNotification) {
            publisher.publishEvent(new LessonAddedEvent(this, event.getUserId(), event));
        }
        return event;
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

    public EventStatusResponse publishEvent(Long eventId, User owner) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(eventId));

        if (event.getStatus().equals(EventStatus.DRAFT)) {
            Event publishedEvent = publish(eventId);
            if (owner.isUser()) {
                boolean publishElastic = checkUserModerationForUserRole(owner, publishedEvent);
                EventModeration eventModeration = eventModerationRepository.findByEventId(publishedEvent.getId()).get();
                if (publishElastic) {
                    eventElasticService.publish(publishedEvent);
                }
                return new EventStatusResponse(true, publishedEvent.getId(), eventModeration);
            } else {
                eventElasticService.publish(publishedEvent);
                return new EventStatusResponse(true, publishedEvent.getId());
            }
        } else {
            throw new IllegalStateException("версия не может быть изменена");
        }
    }
    public void unpublishEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(eventId));
        if (event.getStatus().equals(EventStatus.APPROVED)) {
            event.setStatus(EventStatus.DRAFT);
            eventRepository.save(event);
            eventElasticService.publishDelete(eventId);
        } else {
            throw new IllegalStateException("версия не опубликована");
        }
    }
}
