package kz.btsd.edmarket.online.model;

import lombok.Data;

@Data
public class UserActionResultDto {
    private Integer sectionProgress;

    public UserActionResultDto(Integer sectionProgress) {
        this.sectionProgress = sectionProgress;
    }
}
