package kz.btsd.edmarket.online.module.repository;

import kz.btsd.edmarket.online.module.model.Module;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface ModuleRepository extends CrudRepository<Module, Long> {
    @Query("select s.id from Module s where s.eventId=?1 order by s.position")
    List<Long> findIdsByEventIdOrderByPosition(Long eventId);

    List<Module> findByEventId(Long eventId);

    @Transactional
    @Modifying
    @Query("UPDATE Module m SET m.position = :position WHERE m.id=:id")
    void updatePosition(@Param("id") Long id, @Param("position") Long position);
}
