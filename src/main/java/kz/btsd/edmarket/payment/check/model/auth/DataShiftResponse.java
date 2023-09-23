package kz.btsd.edmarket.payment.check.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataShiftResponse {
    @JsonProperty("Id")
    private Long id;
    //Идентификатор статуса смены 1 - Смена открыта 2 - Смена зарыта
    @JsonProperty("IdStatusShift")
    private Long idStatusShift;
}
