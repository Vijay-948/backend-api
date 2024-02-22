package backend.real_estate.backendapi.repository;

import backend.real_estate.backendapi.entity.UserBo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserBo, Integer> {

    Optional<UserBo> findByEmail(String email);
}
