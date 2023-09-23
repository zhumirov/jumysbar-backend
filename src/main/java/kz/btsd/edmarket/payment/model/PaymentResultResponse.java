package kz.btsd.edmarket.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResultResponse {
    private boolean accepted;
}
