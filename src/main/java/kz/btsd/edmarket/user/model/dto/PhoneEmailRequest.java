package kz.btsd.edmarket.user.model.dto;

import kz.btsd.edmarket.user.model.Platform;
import lombok.Data;

@Data
public class PhoneEmailRequest {

    private String email;
    private String phone;
    private Platform platform;

}
