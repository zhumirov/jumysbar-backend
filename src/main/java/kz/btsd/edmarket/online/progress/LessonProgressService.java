package kz.btsd.edmarket.online.progress;

import kz.btsd.edmarket.online.model.Section;
import kz.btsd.edmarket.online.model.SectionFullDto;
import kz.btsd.edmarket.online.model.Subsection;
import kz.btsd.edmarket.online.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class LessonProgressService {
    @Autowired
    private SectionService sectionService;

    private List<Long> generateRandomExamQuestions(Section section) {
        LinkedList<Subsection> list = new LinkedList<Subsection>(section.getSubsections());
        LinkedList<Long> result = new LinkedList<Long>();
        for (int i = 0; i < section.getExamSize(); i++) {
            int randomNum = ThreadLocalRandom.current().nextInt(0, list.size());
            Subsection subsection = list.get(randomNum);
            list.remove(randomNum);
            result.add(subsection.getId());
        }
        return result;
    }

    public List<Long> generateExamQuestions(Long sectionId) {
        Section section = sectionService.findByIdWithLazy(sectionId);
        if (section.getExamSize() == null || section.getExamSize() >= section.getSubsections().size()) {
            LinkedList<Long> result = new LinkedList<Long>();
            for (Subsection subsection:
                 section.getSubsections()) {
                result.add(subsection.getId());
            }
            return result;
        } else {
            return generateRandomExamQuestions(section);
        }
    }
}
