package kz.btsd.edmarket.mobile.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MinimalUserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String fileId;
}
