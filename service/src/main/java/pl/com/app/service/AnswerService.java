package pl.com.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import pl.com.app.dto.AnswerDTO;
import pl.com.app.exceptions.ExceptionCode;
import pl.com.app.exceptions.MyException;
import pl.com.app.model.Answer;
import pl.com.app.model.Subject;
import pl.com.app.model.User;
import pl.com.app.repository.AnswerRepository;
import pl.com.app.repository.SubjectRepository;
import pl.com.app.repository.UserRepository;
import pl.com.app.service.mappers.AnswerMapper;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final AnswerMapper answerMapper;


    public Map<Long, List<AnswerDTO>> getGroupedAnswers(Collection<Long> subjectIds) {
        try {
            if (subjectIds == null) {
                throw new NullPointerException("SUBJECT ID'S IS NULL");
            }
            return answerRepository.findAllBySubjectIdIn(subjectIds)
                    .stream()
                    .map(answerMapper::answerToAnswerDTO)
                    .collect(Collectors.groupingBy(a -> a.getSubjectDTO().getId()));
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public Map<Long, Optional<AnswerDTO>> getLastAnswerBySubject(Collection<Long> subjectIds) {
        try {
            if (subjectIds == null) {
                throw new NullPointerException("SUBJECT ID'S IS NULL");
            }
            return answerRepository.findAllBySubjectIdIn(subjectIds)
                    .stream()
                    .map(answerMapper::answerToAnswerDTO)
                    .collect(Collectors.groupingBy(a -> a.getSubjectDTO().getId()))
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue()
                                    .stream()
                                    .max(Comparator.comparing(AnswerDTO::getDateTimeAnswer)))
                    );
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public List<AnswerDTO> getAnswersForSubject(Long subjectId) {
        try {
            if (subjectId == null) {
                throw new NullPointerException("SUBJECT ID IS NULL");
            }
            return answerRepository.findAllBySubjectIdOrderByDateTimeAnswer(subjectId)
                    .stream()
                    .map(answerMapper::answerToAnswerDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public void addAnswer(AnswerDTO answerDTO) {
        try {
            if (answerDTO == null) {
                throw new NullPointerException("ANSWER IS NULL");
            }

            if (answerDTO.getUserDTO() == null) {
                throw new NullPointerException("USER IS NULL");
            }

            if (answerDTO.getUserDTO().getId() == null) {
                throw new NullPointerException("USER ID IS NULL");
            }

            if (answerDTO.getSubjectDTO() == null) {
                throw new NullPointerException("SUBJECT IS NULL");
            }

            if (answerDTO.getSubjectDTO().getId() == null) {
                throw new NullPointerException("SUBJECT ID IS NULL");
            }

            User user = userRepository
                    .findById(answerDTO.getUserDTO().getId())
                    .orElseThrow(NullPointerException::new);

            Subject subject = subjectRepository
                    .findById(answerDTO.getSubjectDTO().getId())
                    .orElseThrow(NullPointerException::new);

            Answer answer = answerMapper.answerDTOToAnswer(answerDTO);
            answer.setUser(user);
            answer.setSubject(subject);
            answer.setPositiveGrades(0);
            answer.setNegativeGrades(0);
            answer.setDateTimeAnswer(LocalDateTime.now());

            answerRepository.save(answer);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public void addNegativeGradeToAnswer(Long id) {
        try {
            if (id == null) {
                throw new NullPointerException("ID IS NULL");
            }

            Answer answer = answerRepository
                    .findById(id)
                    .orElseThrow(NullPointerException::new);

            answer.setNegativeGrades(answer.getNegativeGrades() + 1);

            answerRepository.save(answer);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public void addPositiveGradeToAnswer(Long id) {
        try {
            if (id == null) {
                throw new NullPointerException("ID IS NULL");
            }

            Answer answer = answerRepository
                    .findById(id)
                    .orElseThrow(NullPointerException::new);

            answer.setPositiveGrades(answer.getPositiveGrades() + 1);

            answerRepository.save(answer);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public void deleteAnswer(Long answerId) {
        try {
            if (answerId == null) {
                throw new NullPointerException("ANSWER ID IS NULL");
            }

            answerRepository.deleteById(answerId);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public AnswerDTO getOneAnswer(Long answerId) {
        try {
            if (answerId == null) {
                throw new NullPointerException("ANSWER ID IS NULL");
            }

            return answerRepository
                    .findById(answerId)
                    .map(answerMapper::answerToAnswerDTO)
                    .orElseThrow(NullPointerException::new);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public void modifyAnswer(AnswerDTO answerDTO) {
        try {
            if (answerDTO == null) {
                throw new NullPointerException("ANSWER IS NULL");
            }

            if (answerDTO.getUserDTO() == null) {
                throw new NullPointerException("USER IS NULL");
            }

            if (answerDTO.getUserDTO().getId() == null) {
                throw new NullPointerException("USER ID IS NULL");
            }

            if (answerDTO.getSubjectDTO() == null) {
                throw new NullPointerException("SUBJECT IS NULL");
            }

            if (answerDTO.getSubjectDTO().getId() == null) {
                throw new NullPointerException("SUBJECT ID IS NULL");
            }

            User user = userRepository
                    .findById(answerDTO.getUserDTO().getId())
                    .orElseThrow(NullPointerException::new);

            Subject subject = subjectRepository
                    .findById(answerDTO.getSubjectDTO().getId())
                    .orElseThrow(NullPointerException::new);

            Answer answer = answerRepository.findById(answerDTO.getId())
                    .orElseThrow(NullPointerException::new);

            answer.setUser(user);
            answer.setSubject(subject);
            answer.setDateTimeAnswer(LocalDateTime.now());
            answer.setContent(answerDTO.getContent() != null ? answerDTO.getContent() : answer.getContent());
            answer.setNegativeGrades(answerDTO.getNegativeGrades() != null ? answerDTO.getNegativeGrades() : answer.getNegativeGrades());
            answer.setPositiveGrades(answerDTO.getPositiveGrades() != null ? answerDTO.getPositiveGrades() : answer.getPositiveGrades());

            answerRepository.save(answer);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }
}