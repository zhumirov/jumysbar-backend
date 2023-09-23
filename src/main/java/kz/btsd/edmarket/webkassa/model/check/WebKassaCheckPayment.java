package kz.btsd.edmarket.webkassa.model.check;

import lombok.Data;

@Data
public class WebKassaCheckPayment {
    private double sum;
    private int paymentType=1;

    public WebKassaCheckPayment() {
    }
}
