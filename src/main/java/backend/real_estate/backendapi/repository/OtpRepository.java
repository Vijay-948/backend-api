package backend.real_estate.backendapi.repository;

import backend.real_estate.backendapi.entity.OtpBO;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OtpRepository extends CrudRepository<OtpBO, String> {

    Optional<OtpBO> findByEmail(String email);
}
