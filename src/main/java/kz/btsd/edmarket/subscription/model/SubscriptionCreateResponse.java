package kz.btsd.edmarket.subscription.model;

import lombok.Data;

@Data
public class SubscriptionCreateResponse {
    private String id;
    private String url;
    private SubscriptionStatus status;
    private Subscription subscription;

    public enum SubscriptionStatus {
        PAY, CREATED
    }
}
