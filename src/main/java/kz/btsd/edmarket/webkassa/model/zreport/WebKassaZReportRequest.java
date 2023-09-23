package kz.btsd.edmarket.webkassa.model.zreport;

import lombok.Data;

@Data
public class WebKassaZReportRequest {
    private String token;
    private String cashboxUniqueNumber;

    public WebKassaZReportRequest(String token, String cashboxUniqueNumber) {
        this.token = token;
        this.cashboxUniqueNumber = cashboxUniqueNumber;
    }
}
