package kz.btsd.edmarket.webkassa.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WebKassaAuthResponse {
    @JsonProperty("Data")
    private DataWebKassaResponse data;

    public WebKassaAuthResponse() {
    }
}
