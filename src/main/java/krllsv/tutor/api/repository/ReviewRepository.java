package krllsv.tutor.api.repository;

import krllsv.tutor.api.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByTutorId(Long tutorId);
    List<ReviewEntity> findByStudentId(Long studentId);

    @Query("SELECT DISTINCT r FROM ReviewEntity r " +
            "LEFT JOIN FETCH r.student " +
            "LEFT JOIN FETCH r.tutor")
    List<ReviewEntity> findAllWithDetails();
}