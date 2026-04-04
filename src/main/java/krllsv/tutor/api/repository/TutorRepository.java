package krllsv.tutor.api.repository;

import krllsv.tutor.api.entity.TutorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorRepository extends JpaRepository<TutorEntity, Long> {
    @Query("SELECT DISTINCT t FROM TutorEntity t LEFT JOIN FETCH t.subject")
    List<TutorEntity> findAllWithSubject();

    @Query("SELECT DISTINCT t FROM TutorEntity t " +
            "JOIN t.subject sub " +
            "WHERE LOWER(sub.name) LIKE LOWER(CONCAT('%', :subjectName, '%'))")
    Page<TutorEntity> findTutorsBySubjectName(@Param("subjectName") String subjectName, Pageable pageable);

    @Query(value = "SELECT DISTINCT t.* FROM tutors t " +
            "JOIN subjects s ON t.subject_id = s.id " +
            "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :subjectName, '%'))",
            countQuery = "SELECT COUNT(DISTINCT t.id) FROM tutors t " +
                    "JOIN subjects s ON t.subject_id = s.id " +
                    "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :subjectName, '%'))",
            nativeQuery = true)
    Page<TutorEntity> findTutorsBySubjectNameNative(@Param("subjectName") String subjectName, Pageable pageable);
}