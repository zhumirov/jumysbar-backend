package kz.btsd.edmarket.user.model.erg;

import kz.btsd.edmarket.user.model.Platform;
import kz.btsd.edmarket.user.model.UserRole;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class SignupEmployeeDto {
    @NotNull
    @Size(min = 8)
    private String password;

    private Platform platform = Platform.JUMYSBAR;

    private String name;
    private String phone;
    private String email;

    @NotNull
    private String employeeId;
    private String position;
    private String company;
}
