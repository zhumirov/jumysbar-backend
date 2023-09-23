package kz.btsd.edmarket.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TitleAndDescription implements Serializable {
    private String title;
    private String description;
}
