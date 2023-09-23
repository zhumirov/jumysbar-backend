package kz.btsd.edmarket.user.model;

import lombok.Data;

@Data
public class LoginBySmsCodeRequest {
    private String phone;
    private Long verificationId;
    private Long code;
}
