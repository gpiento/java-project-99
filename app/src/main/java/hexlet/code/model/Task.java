package hexlet.code.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Task implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(nullable = false)
    @NotNull
    @Size(min = 1)
    private String name;

    private Integer index;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name = "task_status_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TaskStatus taskStatus;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "assignee_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User assignee;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tasks_labels",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Label> labels = new LinkedHashSet<>();

    @CreatedDate
    private LocalDate createdAt;
}
