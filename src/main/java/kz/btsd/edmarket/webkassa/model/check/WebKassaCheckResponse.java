package kz.btsd.edmarket.webkassa.model.check;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WebKassaCheckResponse {
    @JsonProperty("Data")
    private DataWebKassaCheckResponse data;
    @JsonProperty("Errors")
    private List<ErrorWebKassaCheckResponse> errors;

    public WebKassaCheckResponse() {
    }
}
