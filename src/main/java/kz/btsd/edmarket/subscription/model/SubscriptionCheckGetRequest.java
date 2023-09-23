package kz.btsd.edmarket.subscription.model;

import lombok.Data;

@Data
public class SubscriptionCheckGetRequest {
    private Long userId;
    private Long eventId;
}
