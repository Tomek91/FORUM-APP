package pl.com.app.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import pl.com.app.dto.AnswerDTO;
import pl.com.app.exceptions.ExceptionCode;
import pl.com.app.exceptions.MyException;
import pl.com.app.service.AnswerService;

@Component
@RequiredArgsConstructor
public class AnswerValidator implements Validator {
    private final AnswerService answerService;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(AnswerDTO.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (o == null) {
            throw new MyException(ExceptionCode.VALIDATION, "OBJECT IS NULL");
        }

        AnswerDTO answerDTO = (AnswerDTO) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "content", "content", "Content is not correct");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userDTO", "userDTO", "User is not correct");

        if (answerDTO.getNegativeGrades() != null && answerDTO.getNegativeGrades() < 0) {
            errors.rejectValue("negativeGrades", "Negative grades are not correct");
        }
        if (answerDTO.getPositiveGrades() != null && answerDTO.getPositiveGrades() < 0) {
            errors.rejectValue("positiveGrades", "Positive grades are not correct");
        }

    }
}
