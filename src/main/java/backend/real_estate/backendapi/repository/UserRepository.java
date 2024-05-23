package backend.real_estate.backendapi.repository;

import backend.real_estate.backendapi.entity.UserBo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserBo, Integer> {

    Optional<UserBo> findByEmail(String email);

//    List<UserBo> findAllByInactiveUsers(boolean active, Date createdOn);

//    List<UserBo> findAllByactiveUsers(boolean b, Date cutOfDate);

    List<UserBo> findAllByActiveFalseAndCreatedOnBefore(Date cutoffDate);
}
