package kz.btsd.edmarket.user.model;

import kz.btsd.edmarket.user.model.enums.Activity;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class UserChangePersonalRequest {
    private String name;
    @Size(min = 10, max = 15)
    private String phone;
    @Email
    private String email;
    private String fileId;
    private String firstName;
    private String lastName;
    private String city;
    private Date birthdate;
    private Activity activity;
    private String job;
    private String specialization;
    private String information;
    private String position;
    private String company;
}
