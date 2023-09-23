package kz.btsd.edmarket.online.progress.testhomework;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class UserEventProgressDto {
    private Long id;
    private String name;
    private String email;
    //предприятие для ERG
    private String company;
    private LocalDate userRegistrationDate;
    private Date createdDate;
    private Date lastActivityDate;


    public UserEventProgressDto() {
    }

    public UserEventProgressDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
