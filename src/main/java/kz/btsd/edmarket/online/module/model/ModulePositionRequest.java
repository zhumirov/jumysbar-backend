package kz.btsd.edmarket.online.module.model;

import lombok.Data;

import java.util.List;

/**
 * Раздел
 */
@Data
public class ModulePositionRequest {
    List<ModulePositionDto> modules;

    public ModulePositionRequest() {
    }
}
