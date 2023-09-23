package kz.btsd.edmarket.mobile.model;

import kz.btsd.edmarket.comment.like.model.LikeValue;
import kz.btsd.edmarket.file.model.FileDto;
import kz.btsd.edmarket.mobile.model.enums.SlideType;
import kz.btsd.edmarket.online.model.TestType;
import kz.btsd.edmarket.online.model.UnitType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class SlideDto {
    private Long id;
    private SlideType type;
    private LikeValue like;
    private String question;
    private String answer;
    private List<OptionDto> options;
    private boolean completed;
    private boolean free;
    private String title;
    private String homeWork;
    private String text;
    private FileDto image;
    private String videoUrl;
    private List<String> links;
    private List<FileDto> attachments;
}
