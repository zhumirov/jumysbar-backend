package kz.btsd.edmarket.payment.model;

import lombok.Data;

@Data
public class PaymentCreateRequest {
    private Long userId;
    private Long eventId;
    private String returnUrl;
    private Object metadata;
}
