package kz.btsd.edmarket.webkassa.model.check;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WebKassaCheckRequest {
    @JsonProperty("Token")
    private String token;
    @JsonProperty("CashboxUniqueNumber")
    private String cashboxUniqueNumber;
    private int operationType;
    private int roundType=2;
    private String externalCheckNumber;
    private String customerPhone;
    private List<WebKassaCheckPayment> payments;
    private List<WebKassaCheckPosition> positions;

    public WebKassaCheckRequest() {
    }
}
