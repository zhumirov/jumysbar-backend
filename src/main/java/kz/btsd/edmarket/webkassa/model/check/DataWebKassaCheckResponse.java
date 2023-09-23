package kz.btsd.edmarket.webkassa.model.check;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataWebKassaCheckResponse {
    @JsonProperty("CheckNumber")
    private String checkNumber;
    @JsonProperty("DateTime")
    private String dateTime;
    @JsonProperty("TicketUrl")
    private String ticketUrl;
    @JsonProperty("TicketPrintUrl")
    private String ticketPrintUrl;
}
