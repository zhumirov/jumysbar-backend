package kz.btsd.edmarket.user.model.erg;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class LoginEmployeeRequest {

    @NotEmpty
    private String employeeId;

    @NotEmpty
    @Size(min = 8)
    private String password;
}
