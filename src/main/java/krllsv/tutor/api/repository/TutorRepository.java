package krllsv.tutor.api.repository;

import krllsv.tutor.api.entity.TutorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorRepository extends JpaRepository<TutorEntity, Long> {
    @Query("SELECT DISTINCT t FROM TutorEntity t LEFT JOIN FETCH t.subject")
    List<TutorEntity> findAllWithSubject();

    @EntityGraph(attributePaths = {"subject"})
    @Query("SELECT DISTINCT t FROM TutorEntity t " +
            "WHERE LOWER(t.subject.name) LIKE LOWER(CONCAT('%', :subjectName, '%'))")
    Page<TutorEntity> findTutorsBySubjectName(@Param("subjectName") String subjectName, Pageable pageable);

    @Query(value = "SELECT t.id, t.first_name, t.last_name, t.email, t.hourly_rate, t.start_year, "
            + "s.id as subject_id, s.name as subject_name, s.category as subject_category "
            + "FROM tutors t "
            + "JOIN subjects s ON t.subject_id = s.id "
            + "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :subjectName, '%'))",
            countQuery = "SELECT COUNT(*) FROM tutors t "
                    + "JOIN subjects s ON t.subject_id = s.id "
                    + "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :subjectName, '%'))",
            nativeQuery = true)
    Page<Object[]> findTutorsBySubjectNameNative(@Param("subjectName") String subjectName, Pageable pageable);
}