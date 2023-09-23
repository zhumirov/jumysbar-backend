package kz.btsd.edmarket.comment.like.service;

import kz.btsd.edmarket.online.repository.SubsectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class StepLikeAsyncService {
    @Autowired
    private SubsectionRepository subsectionRepository;

    //todo переделать на апдейт в один шаг через базу
    @Async
    public void update(Long stepId) {
        subsectionRepository.updateSteptLikeAndDislike(stepId);
    }
}
