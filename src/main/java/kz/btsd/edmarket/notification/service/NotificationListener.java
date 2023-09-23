package kz.btsd.edmarket.notification.service;

import kz.btsd.edmarket.comment.model.Comment;
import kz.btsd.edmarket.comment.repository.CommentRepository;
import kz.btsd.edmarket.comment.repository.CommentService;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.mentor.model.Mentor;
import kz.btsd.edmarket.mentor.repository.MentorRepository;
import kz.btsd.edmarket.notification.model.Notification;
import kz.btsd.edmarket.notification.repository.NotificationRepository;
import kz.btsd.edmarket.online.model.Section;
import kz.btsd.edmarket.online.model.Subsection;
import kz.btsd.edmarket.online.module.model.Module;
import kz.btsd.edmarket.online.module.repository.ModuleRepository;
import kz.btsd.edmarket.online.repository.SectionRepository;
import kz.btsd.edmarket.online.repository.SubsectionRepository;
import kz.btsd.edmarket.subscription.model.Subscription;
import kz.btsd.edmarket.subscription.repository.SubscriptionRepository;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NotificationListener {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private SubsectionRepository subsectionRepository;
    @Autowired
    private MentorRepository mentorRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private ModuleRepository moduleRepository;

    @Async
    @EventListener
    public void processMentorCreatedEvent(MentorCreatedEvent event) {
        Mentor mentor = event.getMentor();
        if (mentor.isRegistered()) {
            String eventTitle = eventRepository.findById(mentor.getEventId()).get().getTitle();
            Notification notification = new Notification(mentor.getUserId(), event.getSenderId(), "Вас сделали ментором курса " + eventTitle);
            notificationRepository.save(notification);
        }
    }

    private String createLink(Long stepId) {
        Subsection subsection = subsectionRepository.findById(stepId).get();
        Section section = sectionRepository.findById(subsection.getSectionId()).get();
        Module module = moduleRepository.findById(section.getModuleId()).get();
        String link = "/online-course/"+module.getEventId()+"?l="+section.getPosition()+"&m="+module.getPosition()+"&s="+subsection.getPosition();
        return link;
    }

    private String createCommentText(Comment comment, String eventTitle) {
        User user = userRepository.findById(comment.getUserId()).get();
        String text;
        if (comment.isHomework()) {
            String link = createLink(comment.getStepId());
            text = "Пользователь " + user.getName() + " отправил <a href=\""+link+"\">домашнюю работу</a>.";
        } else {
            if (comment.getStepId() != null) {
                String link = createLink(comment.getStepId());
                text = "Пользователь " + user.getName() + " оставил <a href=\""+link+"\">комментарий</a>.";
            } else {
                text = "Пользователь " + user.getName() + " оставил комментарий";
            }
        }
        text += " Курс " + eventTitle + ".";
        if (comment.getStepId() != null) {
            Subsection subsection = subsectionRepository.findById(comment.getStepId()).get();
            text += " Шаг " + subsection.getTitle() + ".";
        } else {
            Section section = sectionRepository.findById(comment.getLessonId()).get();
            text += " Урок " + section.getTitle() + ".";
        }
        return text;
    }

    @Async
    @EventListener
    public void processCommentCreatedEvent(CommentCreatedEvent eventApp) {
        Comment comment = eventApp.getComment();
        Event event = commentService.getEvent(comment);
        String text = createCommentText(comment, event.getTitle());

        Set<Long> recipients = eventOwnerAndMentors(event.getId(), event.getUserId());

        if (comment.getAnswerToId() != null) { //уведомить кому отвечаем
            Comment answer = commentRepository.findById(comment.getAnswerToId()).get();
            recipients.add(answer.getUserId());
        }

        recipients.remove(comment.getUserId()); // не уведомляем сами себя

        notificationService.notify(recipients, eventApp.getSenderId(), text);
    }

    @Async
    @EventListener
    public void processLessonAddedEvent(LessonAddedEvent eventApp) {
        Event event = eventApp.getEvent();
        String text = "Добавились новые уроки в курсе: "+ event.getTitle();

        Set<Long> recipients = eventOwnerAndMentors(event.getId(), event.getUserId());

        List<Subscription> subscriptions = subscriptionRepository.findAllByEventId(event.getId());
        for (Subscription subscription :
                subscriptions) {
            recipients.add(subscription.getUserId());
        }
        recipients.remove(event.getUserId()); // не уведомляем сами себя

        notificationService.notify(recipients, eventApp.getSenderId(), text);
    }

    private Set<Long> eventOwnerAndMentors(Long eventId, Long eventOwnerId) {
        Set<Long> recipients = new HashSet<>();
        //сообщение создателю курса
        recipients.add(eventOwnerId);

        List<Mentor> mentors = mentorRepository.findByEventId(eventId);//сообщение менторам
        for (Mentor mentor :
                mentors) {
            recipients.add(mentor.getUserId());
        }
        return recipients;
    }

    @Async
    @EventListener
    public void processSubscriptionAddedEvent(SubscriptionAddedEvent eventApp) {
        Subscription subscription = eventApp.getSubscription();
        Event event = eventRepository.findById(subscription.getEventId()).get();
        User user = userRepository.findById(subscription.getUserId()).get();
        String text = user.getName() + " подписался на курс:"+ event.getTitle();

        Set<Long> recipients = eventOwnerAndMentors(event.getId(), event.getUserId());

        recipients.remove(subscription.getUserId()); // не уведомляем сами себя

        notificationService.notify(recipients, eventApp.getSenderId(), text);
    }
}
