package krllsv.tutor.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "subjects")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "category")
    private String category;
    @Column(name = "description", length = 1000)
    private String description;

    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY)
    private List<TutorEntity> tutors = new ArrayList<>();
}
