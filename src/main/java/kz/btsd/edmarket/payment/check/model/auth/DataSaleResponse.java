package kz.btsd.edmarket.payment.check.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataSaleResponse {
    //Номер чека
    @JsonProperty("IdDocument")
    private Long idDocument;
    //Картинка с чеком в base64
    @JsonProperty("Receipt")
    private String receipt;
    //ссылка на чек
    @JsonProperty("Location")
    private String location;
}
