package kz.btsd.edmarket.user.repository;

import kz.btsd.edmarket.user.model.Platform;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserShortDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("select u from user_ed u where u.deleted = false and u.id = :id")
    Optional<User> findById(Long id);
    @Query("select u from user_ed u where u.id = :id")
    Optional<User> getUserBy(Long id);
    List<User> findByCreatedDateAfterAndDeletedFalse(Date createdDate, Pageable pageable);

    long countByPlatformAndCreatedDateAfterAndDeletedFalse(Platform platform, Date createdDate);

    long countByCreatedDateAfterAndDeletedFalse(Date createdDate);

    @Query("select new kz.btsd.edmarket.user.model.UserShortDto(u.id, u.name) from user_ed u where u.id = :userId and u.deleted = false ")
    Optional<UserShortDto> findByIdShortDto(@Param("userId") Long userId);

    Optional<User> findByPhoneAndDeletedFalse(String phone);
    Optional<User> findByPhoneAndPlatformAndDeletedFalse(String phone, Platform platform);

    @Query("select u from user_ed u where u.deleted = false and  u.platform=:platform and " +
            "(:query is null or (lower(u.name) LIKE %:query% or lower(u.phone) LIKE %:query% or lower(u.email) LIKE %:query%))")
    List<User> findByPlatform(@Param("platform") Platform platform, @Param("query") String query, Pageable pageable);

    @Query("select count(u) from user_ed u where u.deleted = false and u.platform=:platform and " +
            "(:query is null or (lower(u.name) LIKE %:query% or lower(u.phone) LIKE %:query% or lower(u.email) LIKE %:query%))")
    long countByPlatform(@Param("platform") Platform platform, @Param("query") String query);

    long countByPlatformAndAituUserIdIsNotNullAndDeletedFalse(@Param("platform") Platform platform);

    long countByPlatformAndAituUserIdIsNullAndDeletedFalse(@Param("platform") Platform platform);

    List<User> findByPlatformAndAituUserIdIsNullAndDeletedFalse(@Param("platform") Platform platform, Pageable pageable);

    long countByPlatformAndDeletedFalse(@Param("platform") Platform platform);

    Optional<User> findByEmailAndDeletedFalse(String email);
    Optional<User> findByEmailAndPlatformAndDeletedFalse(String email, Platform platform);

    Optional<User> findByEmployeeIdAndDeletedFalse(String employeeId);

    @Query("select u from user_ed u join Subscription s on u.id = s.userId where u.deleted = false and s.eventId=?1")
    List<User> findAllSubscriptionUser(Long eventId);

    boolean existsByPhoneAndDeletedFalse(String phone);
    boolean existsByPhoneAndPlatformAndDeletedFalse(String phone, Platform platform);
    boolean existsByEmailAndDeletedFalse(String email);
    boolean existsByEmailAndPlatformAndDeletedFalse(String email, Platform platform);
    boolean existsByEmployeeIdAndDeletedFalse(String employeeId);
    @Query("select u from user_ed u where u.deleted = false")
    List<User> findAll(Pageable pageable);

}
