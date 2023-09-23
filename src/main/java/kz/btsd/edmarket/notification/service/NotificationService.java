package kz.btsd.edmarket.notification.service;

import kz.btsd.edmarket.notification.model.Notification;
import kz.btsd.edmarket.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public void notify(Set<Long> recipients, Long senderId, String text) {
        Set<Notification> notifications = new HashSet<>();
        for (Long recipient :
                recipients) {
            notifications.add(new Notification(recipient, senderId, text));
        }
        notificationRepository.saveAll(notifications);
    }

    public void notify(long userId, Long senderId, String text) {
        Notification notification = new Notification(userId, senderId, text);
        notificationRepository.save(notification);
    }

}
