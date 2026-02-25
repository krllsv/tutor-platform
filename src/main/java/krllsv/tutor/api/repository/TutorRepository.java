package krllsv.tutor.api.repository;

import jakarta.annotation.PostConstruct;
import krllsv.tutor.api.domain.Tutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TutorRepository {

    private final List<Tutor> tutors = new ArrayList<>();

    @PostConstruct
    public void init() {
        tutors.add(new Tutor(
                1L,
                "Ivan",
                "Ivanov",
                "Math",
                new BigDecimal("1500"),
                5,
                4.8,
                "ivanivanov@gmail.com"
        ));

        tutors.add(new Tutor(
                2L,
                "Anna",
                "Pavlovna",
                "Chemistry",
                new BigDecimal("1300"),
                8,
                4.5,
                "annapavlovna@gmail.com"
        ));

        tutors.add(new Tutor(
                3L,
                "Andrei",
                "Vladimirovich",
                "Physics",
                new BigDecimal("1900"),
                17,
                4.9,
                "andreivladimirovich@gmail.com"
        ));
    }

    public ArrayList<Tutor> findAll() {
        return new ArrayList<>(tutors);
    }

    public Optional<Tutor> findById(Long id) {
        return tutors.stream()
                .filter(tutor -> tutor.getId().equals(id))
                .findFirst();
    }

    public List<Tutor> findBySubject(String subject) {
        return tutors.stream()
                .filter(tutor -> tutor.getSubject().toLowerCase().contains(subject.toLowerCase()))
                .toList();
    }
}
