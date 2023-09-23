package kz.btsd.edmarket.user.model;

import kz.btsd.edmarket.user.model.enums.Activity;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String phone;
    private boolean phoneConfirmed = true;
    private String email;
    private boolean emailConfirmed = false;
    private String firstName;
    private String lastName;
    private String city;
    private Date birthdate;
    private Activity activity;
    private String job;
    private Platform platform = Platform.JUMYSBAR;
    //todo профиль преподавателя. - также там изображение есть
    private String specialization;
    private String information;

    //БИН/ИИН - для ecommerce/mastercard
    private String identifier;
    //должность для ERG
    private String position;
    //предприятие для ERG
    private String company;
    private String employeeId;
    private boolean systemPassword=false;
    //для datalake aitu User Id
    private String aituUserId;
    //aitu User Token
    private String aituTokenId;

    //todo убрать когда отдаеш информацию с токеном
        @Field(type = FieldType.Keyword)
    private String fileId;

    @Enumerated(EnumType.STRING)
    private UserRole userRole = UserRole.USER;

    private Date createdDate;
    private Date lastActivityDate;

    public UserDto() {
    }
}
