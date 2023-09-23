package kz.btsd.edmarket.user.model;

import lombok.Data;

@Data
public class UserAddOrgInfoDto {
    private Long id;
    private String name;
    private String specialization;
    private String information;
    private String fileId;
    private String email;
}
