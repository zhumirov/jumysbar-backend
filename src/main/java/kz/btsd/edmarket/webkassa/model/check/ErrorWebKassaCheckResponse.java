package kz.btsd.edmarket.webkassa.model.check;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ErrorWebKassaCheckResponse {
    private int code;
    @JsonProperty("Text")
    private String text;
}
