package pl.com.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.com.app.dto.SearchSubjectDTO;
import pl.com.app.dto.SubjectDTO;
import pl.com.app.exceptions.ExceptionCode;
import pl.com.app.exceptions.MyException;
import pl.com.app.model.Answer;
import pl.com.app.model.Subject;
import pl.com.app.model.User;
import pl.com.app.repository.AnswerRepository;
import pl.com.app.repository.SubjectRepository;
import pl.com.app.repository.UserRepository;
import pl.com.app.service.mappers.SubjectMapper;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final SubjectMapper subjectMapper;

    public List<SubjectDTO> getAllSubjects() {
        try {
            return subjectRepository.findAll()
                    .stream()
                    .map(subjectMapper::subjectToSubjectDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public void addSubject(SubjectDTO subjectDTO){
        try {
            if (subjectDTO == null) {
                throw new NullPointerException("SUBJECT IS NULL");
            }

            if (subjectDTO.getUserDTO() == null) {
                throw new NullPointerException("USER IS NULL");
            }

            if (subjectDTO.getUserDTO().getId() == null) {
                throw new NullPointerException("USER ID IS NULL");
            }

            User user = userRepository
                    .findById(subjectDTO.getUserDTO().getId())
                    .orElseThrow(NullPointerException::new);

            Subject subject = subjectMapper.subjectDTOToSubject(subjectDTO);
            subject.setUser(user);
            subject.setDateTimeStart(LocalDateTime.now());


            subjectRepository.save(subject);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public SubjectDTO getOneSubject(Long id) {
        try {
            if (id == null) {
                throw new NullPointerException("SUBJECT ID IS NULL");
            }

            return subjectRepository
                    .findById(id)
                    .map(subjectMapper::subjectToSubjectDTO)
                    .orElseThrow(NullPointerException::new);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public void deleteSubject(Long subjectId) {
        try{
            if (subjectId == null) {
                throw new NullPointerException("SUBJECT ID IS NULL");
            }

            List<Answer> answersToDelete = answerRepository.findAllBySubjectIdOrderByDateTimeAnswer(subjectId);
            answerRepository.deleteAll(answersToDelete);

            subjectRepository.deleteById(subjectId);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public void modifySubject(SubjectDTO subjectDTO) {
        try {
            if (subjectDTO == null) {
                throw new NullPointerException("SUBJECT IS NULL");
            }

            if (subjectDTO.getUserDTO() == null) {
                throw new NullPointerException("USER IS NULL");
            }

            if (subjectDTO.getUserDTO().getId() == null) {
                throw new NullPointerException("USER ID IS NULL");
            }

            User user = userRepository
                    .findById(subjectDTO.getUserDTO().getId())
                    .orElseThrow(NullPointerException::new);

            Subject subject = subjectRepository.findById(subjectDTO.getId())
                    .orElseThrow(NullPointerException::new);

            subject.setUser(user);
            subject.setDateTimeStart(LocalDateTime.now());
            subject.setContent(subjectDTO.getContent() != null ? subjectDTO.getContent() : subject.getContent());
            subject.setTitle(subjectDTO.getTitle() != null ? subjectDTO.getTitle() : subject.getTitle());

            subjectRepository.save(subject);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public Page<SubjectDTO> findPage(PageRequest pageRequest) {
        try{
            Page<Subject> page = subjectRepository.findAll(pageRequest);

            List<SubjectDTO> subjectDTOList = page.getContent()
                    .stream()
                    .map(subjectMapper::subjectToSubjectDTO)
                    .collect(Collectors.toList());

            return new PageImpl<>(subjectDTOList, pageRequest, page.getTotalElements());

        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public List<SubjectDTO> findSubjectsByTitle(SearchSubjectDTO searchSubjectDTO) {
        try{
            if (searchSubjectDTO == null) {
                throw new NullPointerException("SUBJECT ID IS NULL");
            }
            if (searchSubjectDTO.getTitle() == null) {
                throw new NullPointerException("TITLE IS NULL");
            }

            return subjectRepository.findByTitleContainingOrderByDateTimeStartDesc(searchSubjectDTO.getTitle().trim())
                    .stream()
                    .map(subjectMapper::subjectToSubjectDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }
}
