package kz.btsd.edmarket.mobile.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OptionDto {
    private String value;
    private String hint;
    private boolean isCorrect;
}
