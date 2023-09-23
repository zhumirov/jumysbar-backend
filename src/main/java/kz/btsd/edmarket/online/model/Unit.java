package kz.btsd.edmarket.online.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Unit implements Serializable {
    // текстовый блок, ссылка, в зависимости от типа
    private String value;
    private String fileName;
    private boolean p2p;
    private boolean homeworkGrade;
    private boolean hiddenHomework;
    @JsonFormat(timezone = "GMT+06:00")
    private Date endDate;
    // для тестов, варианты ответов
    private List<String> answers;
    // для тестов, варианты опций
    private List<Option> options;
    private TestType testType;
    @JsonIgnore
    private String title;

    private UnitType type;

    public Unit(String value, UnitType type) {
        this.value = value;
        this.type = type;
    }
}
