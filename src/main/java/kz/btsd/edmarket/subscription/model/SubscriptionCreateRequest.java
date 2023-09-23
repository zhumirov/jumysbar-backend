package kz.btsd.edmarket.subscription.model;

import lombok.Data;

@Data
public class SubscriptionCreateRequest {
    private Long userId;
    private Long eventId;
    private Long planId;
    private String returnUrl;
    private String promocodeTitle;

    public SubscriptionCreateRequest() {
    }

    public SubscriptionCreateRequest(Long userId, Long eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }
}
