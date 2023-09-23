package kz.btsd.edmarket.common;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

@Data
@Entity
public class ExceptionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exp_seq")
    @SequenceGenerator(name = "exp_seq", sequenceName = "exp_seq",
            allocationSize = 1)
    private Long id;
    private String message;
    private String stackTrace;
    private boolean viewed;

    public ExceptionLog(String message, String stackTrace) {
        this.message = message;
        this.stackTrace = stackTrace;
    }

    public ExceptionLog() {
    }

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();
}

