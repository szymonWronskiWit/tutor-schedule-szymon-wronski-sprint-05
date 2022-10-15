package tutorschedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tutorschedule.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
