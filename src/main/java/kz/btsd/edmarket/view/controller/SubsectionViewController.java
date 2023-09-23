package kz.btsd.edmarket.view.controller;

import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.view.model.SubsectionView;
import kz.btsd.edmarket.view.repository.SubsectionViewRepository;
import kz.btsd.edmarket.view.service.SubsectionViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
public class SubsectionViewController {
    @Autowired
    private SubsectionViewRepository subsectionViewRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private SubsectionViewService subsectionViewService;


//    @GetMapping("/subsection-views/value/{subsectionId}")
//    public long findBySubsectionId(@PathVariable Long subsectionId) {
//        return subsectionViewService.findValueBySubsectionId(subsectionId);
//    }

    private long updateValue(Long subsectionid) {
        return subsectionViewRepository.incrementViewValue(subsectionid);
    }

    @PostMapping("/subsection-views/action/{subsectionId}")
    public long action(@PathVariable Long subsectionId) {
        Optional<SubsectionView> optional = subsectionViewRepository.findBySubsectionId(subsectionId);
        if (optional.isPresent()) {
            return updateValue(subsectionId);
        } else {
            try {
                Event event = eventRepository.findByStepId(subsectionId);
                SubsectionView subsectionView = new SubsectionView(event.getId(), subsectionId, 1l);
                subsectionViewRepository.save(subsectionView);
                return subsectionView.getValue();
            } catch (DataIntegrityViolationException ex) { //на случай одновременного запроса  создания
                return updateValue(subsectionId);
            }
        }
    }

}
