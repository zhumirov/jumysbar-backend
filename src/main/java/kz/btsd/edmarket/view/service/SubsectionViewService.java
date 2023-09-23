package kz.btsd.edmarket.view.service;

import kz.btsd.edmarket.view.model.SubsectionView;
import kz.btsd.edmarket.view.repository.SubsectionViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubsectionViewService {
    @Autowired
    private SubsectionViewRepository subsectionViewRepository;

    public long findValueBySubsectionId(Long subsectionId) {
        Optional<SubsectionView> optional = subsectionViewRepository.findBySubsectionId(subsectionId);
        return optional.isPresent() ? optional.get().getValue() : 0;
    }
}
