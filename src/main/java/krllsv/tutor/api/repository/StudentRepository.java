package krllsv.tutor.api.repository;

import krllsv.tutor.api.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    @Query("SELECT DISTINCT s FROM StudentEntity s LEFT JOIN FETCH s.subjects")
    List<StudentEntity> findAllWithSubjects();

    @Query("SELECT DISTINCT s FROM StudentEntity s LEFT JOIN FETCH s.bookings")
    List<StudentEntity> findAllWithBookings();

    @Query("SELECT s FROM StudentEntity s " +
            "LEFT JOIN FETCH s.subjects " +
            "WHERE s.id = :id")
    Optional<StudentEntity> findByIdWithAll(@Param("id") Long id);
}
