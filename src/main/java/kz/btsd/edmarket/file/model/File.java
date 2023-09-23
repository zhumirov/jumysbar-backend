package kz.btsd.edmarket.file.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

//todo добавить провверку на форматы файлов(изображения)
@Data
@Entity(name = "file_ed")
public class File {
    @Id
    private String id;

    private String fileName;

    private byte[] data;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public File() {
    }

    public File(String id, String fileName, byte[] data) {
        this.id = id;
        this.fileName = fileName;
        this.data = data;
    }
}
