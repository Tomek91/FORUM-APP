package pl.com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.com.app.model.Answer;

import java.util.Collection;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAllBySubjectIdOrderByDateTimeAnswer(Long subjectId);


    List<Answer> findAllBySubjectIdIn(Collection<Long> subjectIds);

}
