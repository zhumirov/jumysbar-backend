package kz.btsd.edmarket.webkassa.model;

import lombok.Data;

@Data
public class CheckInfo {
    private String externalCheckNumber;
    private String customerPhone;
    private String positionName;
    private String price;
}
