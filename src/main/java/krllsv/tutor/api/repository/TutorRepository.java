package krllsv.tutor.api.repository;

import krllsv.tutor.api.entity.TutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorRepository extends JpaRepository<TutorEntity, Long> {
    @Query("SELECT DISTINCT t FROM TutorEntity t LEFT JOIN FETCH t.subject")
    List<TutorEntity> findAllWithSubject();

    @Query("SELECT DISTINCT t FROM TutorEntity t LEFT JOIN FETCH t.reviews")
    List<TutorEntity> findAllWithReviews();

    @Query("SELECT DISTINCT t FROM TutorEntity t LEFT JOIN FETCH t.bookings")
    List<TutorEntity> findAllWithBookings();
}