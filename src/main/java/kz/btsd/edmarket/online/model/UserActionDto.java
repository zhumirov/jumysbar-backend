package kz.btsd.edmarket.online.model;

import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Раздел
 */
@Data
public class UserActionDto {
    private Long id;
    private Long userId;
    private Long eventId;

    public UserActionDto() {
    }
}
