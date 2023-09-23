package kz.btsd.edmarket.user.model;

import kz.btsd.edmarket.user.model.enums.Activity;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name = "user_ed")
@SQLDelete(sql = "UPDATE user_ed SET deleted = true WHERE id = ?")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_ed_seq")
    @SequenceGenerator(name = "user_ed_seq", sequenceName = "user_ed_seq",
            allocationSize = 1)
    private Long id;
    private String phone;
    private boolean phoneConfirmed = true;
    private String password; //todo убрать передачу
    private String name;
    private String email;
    private boolean emailConfirmed = false;
    private String firstName;
    private String lastName;
    private String city;
    private Date birthdate;
    private Activity activity;
    private String job;
    @Enumerated(EnumType.STRING)
    private Platform platform = Platform.JUMYSBAR;
    @Enumerated(EnumType.STRING)
    private UserRole userRole = UserRole.USER;
    //todo профиль преподавателя. - также там изображение есть

    private String specialization;
    private String information;

    //БИН/ИИН - для ecommerce/mastercard
    private String identifier;

    //должность для ERG
    private String position;
    //предприятие для ERG
    private String company;
    //табельный номер
    private String employeeId;
    //сгенерированный пароль-(true)/введенный пользователем(false)
    private boolean systemPassword=false;

    //для datalake aitu User Id
    private String aituUserId;
    //aitu User Token
    private String aituTokenId;

    private String fileId;

    private Boolean deleted = Boolean.FALSE;

    //todo убрать в Authentication
    public boolean isOperator() {
        return UserRole.OPERATOR.equals(userRole);
    }

    public boolean isORG() {
        return UserRole.ORG.equals(userRole);
    }

    //todo убрать в Authentication
    public boolean isAdmin() {
        return UserRole.ADMIN.equals(userRole);
    }

    //todo убрать в Authentication
    public boolean isUser() {
        return UserRole.USER.equals(userRole);
    }

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    @CreatedDate //todo - еше не работает
    private Date lastActivityDate = new Date();

    public User() {
    }

    public User(String phone, String password, String email) {
        this.phone = phone;
        this.password = password;
        this.email = email;
    }

    public User(String phone, String password, String email, String name, Platform platform) {
        this.phone = phone;
        this.password = password;
        this.email = email;
        this.name = name;
        this.platform = platform;
    }
}
