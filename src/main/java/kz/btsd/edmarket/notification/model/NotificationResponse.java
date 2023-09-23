package kz.btsd.edmarket.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class NotificationResponse {
    private List<NotificationDto> hits;
    private long totalHits;
}
