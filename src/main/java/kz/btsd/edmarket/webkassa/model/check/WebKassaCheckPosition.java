package kz.btsd.edmarket.webkassa.model.check;

import lombok.Data;

@Data
public class WebKassaCheckPosition {
    private int count;
    private double price;
    private int taxPercent;
    private Double tax;
    private int taxType=100;
    private String positionName;
    private String positionCode;
    private Long unitCode = 796L;

    public WebKassaCheckPosition() {
    }
}
