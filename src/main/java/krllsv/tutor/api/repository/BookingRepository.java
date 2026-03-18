package krllsv.tutor.api.repository;

import krllsv.tutor.api.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findByTutorId(Long tutorId);
    List<BookingEntity> findByStudentId(Long studentId);

    List<BookingEntity> findByTutorIdAndStatusIn(Long tutorId, List<String> statuses);

    @Query("SELECT DISTINCT b FROM BookingEntity b " +
            "LEFT JOIN FETCH b.student " +
            "LEFT JOIN FETCH b.tutor")
    List<BookingEntity> findAllWithDetails();

    @Query("SELECT b FROM BookingEntity b " +
            "LEFT JOIN FETCH b.student " +
            "LEFT JOIN FETCH b.tutor " +
            "WHERE b.id = :id")
    Optional<BookingEntity> findByIdWithDetails(@Param("id") Long id);
}