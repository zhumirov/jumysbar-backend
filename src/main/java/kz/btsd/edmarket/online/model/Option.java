package kz.btsd.edmarket.online.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Option implements Serializable {
    // вариант ответа
    private String value;
    //подсказка для варианта ответа
    private String hint;
}
