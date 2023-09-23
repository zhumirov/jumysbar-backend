package kz.btsd.edmarket.subscription.model;

import lombok.Data;

@Data
public class SubscriptionCheckResponse {
    private boolean present;
    private Subscription subscription;
}
