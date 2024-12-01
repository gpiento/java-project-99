package hexlet.code.repository;

import hexlet.code.dto.label.LabelDTO;
import hexlet.code.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    Optional<Label> findByName(String name);

    boolean existsByName(String slug);

    Set<Label> findByIdIn(Set<Long> labelIds);

    @Query("select new hexlet.code.dto.label.LabelDTO(l.name) from Label l")
    List<LabelDTO> findAllDto();
}
