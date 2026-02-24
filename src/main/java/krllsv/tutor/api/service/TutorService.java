package krllsv.tutor.api.service;

import krllsv.tutor.api.domain.Tutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TutorService {

    private final Map<Long, Tutor> tutorMap;
    private final AtomicLong idCounter;

    public TutorService(){
        tutorMap = new HashMap<>();
        idCounter = new AtomicLong();
    }

    public Tutor getTutorById(
            Long id
    ) {
        if(!tutorMap.containsKey(id)){
            throw new NoSuchElementException("Not found reservation by id = " + id);
        }
        return tutorMap.get(id);
    }

    public List<Tutor> findAllTutors() {
        return tutorMap.values().stream().toList();
    }

    public Tutor createTutor(Tutor tutorToCreate) {
        if(tutorToCreate != null){
            throw new IllegalArgumentException("Id should be empty");
        }
        var newTutor = new Tutor(
                idCounter.incrementAndGet(),
                tutorToCreate.getFirstName(),
                tutorToCreate.getLastName(),
                tutorToCreate.getSubject(),
                tutorToCreate.getHourlyRate(),
                tutorToCreate.getExperienceYears(),
                tutorToCreate.getRating(),
                tutorToCreate.getEmail()
        );
        tutorMap.put(newTutor.getId(), newTutor);
        return newTutor;
    }
}
