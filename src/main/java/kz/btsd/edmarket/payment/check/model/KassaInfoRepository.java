package kz.btsd.edmarket.payment.check.model;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface KassaInfoRepository extends CrudRepository<KassaInfo, Long> {
    List<KassaInfo> findAll();
    @Transactional
    @Modifying
    @Query("UPDATE KassaInfo ki SET ki.uid = :uid WHERE ki.idKkm=:kkm")
    void updateUid(@Param("uid") String uid, @Param("kkm") Long kkm);
}
