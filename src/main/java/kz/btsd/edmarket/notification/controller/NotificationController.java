package kz.btsd.edmarket.notification.controller;

import kz.btsd.edmarket.common.controller.utils.SortUtils;
import kz.btsd.edmarket.notification.model.Notification;
import kz.btsd.edmarket.notification.model.NotificationConverter;
import kz.btsd.edmarket.notification.model.NotificationResponse;
import kz.btsd.edmarket.notification.model.NotificationStatus;
import kz.btsd.edmarket.notification.repository.NotificationRepository;
import kz.btsd.edmarket.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
public class NotificationController {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationConverter notificationConverter;
    @Autowired
    private AuthService authService;

    /**
     * пометить как прочитанное, id - уведомления
     */
    @PutMapping("/notifications/viewed/{id}")
    public ResponseEntity<?> save(Authentication authentication,  @PathVariable Long id) {
        Notification notification = notificationRepository.findById(id).get();
        authService.checkOwner(authentication.getName(), notification.getUserId());
        notificationRepository.viewed(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/notifications")
    public NotificationResponse list(Authentication authentication,
                                     @RequestParam Long userId,
                                     @RequestParam(defaultValue = "false", required = false) boolean all,
                                     @RequestParam(defaultValue = "0", required = false) Integer page,
                                     @RequestParam(defaultValue = "20", required = false) Integer size,
                                     @RequestParam(defaultValue = "asc", required = false) String order) {
        //authService.checkOwner(authentication.getName(), userId);
        Pageable pageable = PageRequest.of(page, size, SortUtils.buildSort("createdDate", order));
        long count;
        List<Notification> list;
        if (all) {
            list = notificationRepository.findByUserId(userId, pageable);
            count = notificationRepository.countByUserId(userId);
        } else {
            list = notificationRepository.findByUserIdAndStatus(userId, NotificationStatus.NEW, pageable);
            count = notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.NEW);

        }
        return new NotificationResponse(list.stream().map(notificationConverter::convertToDto).collect(Collectors.toList()),
                count);
    }

    @GetMapping(value = "/notifications/count")
    public long count(Authentication authentication, @RequestParam Long userId,
                      @RequestParam(defaultValue = "false", required = false) boolean all) {
        authService.checkOwner(authentication.getName(), userId);
        if (all) {
            return notificationRepository.countByUserId(userId);

        } else {
            return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.NEW);
        }
    }
}
