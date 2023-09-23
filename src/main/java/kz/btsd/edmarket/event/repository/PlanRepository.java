package kz.btsd.edmarket.event.repository;

import kz.btsd.edmarket.event.model.Plan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PlanRepository extends CrudRepository<Plan, Long> {
    Optional<Plan> findByParentId(Long parentId);

}
