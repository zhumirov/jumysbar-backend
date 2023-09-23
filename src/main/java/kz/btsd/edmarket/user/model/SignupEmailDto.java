package kz.btsd.edmarket.user.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class SignupEmailDto {
    @NotNull
    @Size(min = 8)
    private String password;

    private Platform platform = Platform.JUMYSBAR;

    private String name;
//    @NotNull
    private String phone;
    @NotNull
    private String email;

    private String identifier;

    private String company;

    private String bitrixPage;
}
