package hexlet.code.repository;

import hexlet.code.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    Optional<TaskStatus> findBySlug(String slug);

    Optional<TaskStatus> findByName(String name);

    Optional<TaskStatus> findByNameOrSlug(String name, String slug);

    boolean existsBySlug(String slug);
}
