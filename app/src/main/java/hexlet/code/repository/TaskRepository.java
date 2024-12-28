package hexlet.code.repository;

import hexlet.code.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface TaskRepository extends
        JpaRepository<Task, Long>,
        JpaSpecificationExecutor<Task> {

    Optional<Task> findByName(String name);

    boolean existsByName(String name);
}
