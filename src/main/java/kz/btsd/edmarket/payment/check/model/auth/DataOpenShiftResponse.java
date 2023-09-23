package kz.btsd.edmarket.payment.check.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataOpenShiftResponse {
    //Данные созданной смены
    @JsonProperty("Shift")
    private ShiftResponse shift;
}
