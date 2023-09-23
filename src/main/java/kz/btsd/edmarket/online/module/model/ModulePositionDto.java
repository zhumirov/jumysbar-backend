package kz.btsd.edmarket.online.module.model;

import lombok.Data;

/**
 * Раздел
 */
@Data
public class ModulePositionDto {
    private long id;
    private long position;

    public ModulePositionDto() {
    }
}
