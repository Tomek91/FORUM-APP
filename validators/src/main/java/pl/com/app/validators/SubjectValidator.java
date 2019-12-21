package pl.com.app.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import pl.com.app.dto.SubjectDTO;
import pl.com.app.exceptions.ExceptionCode;
import pl.com.app.exceptions.MyException;
import pl.com.app.service.SubjectService;

@Component
@RequiredArgsConstructor
public class SubjectValidator implements Validator {
    private final SubjectService subjectService;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(SubjectDTO.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (o == null) {
            throw new MyException(ExceptionCode.VALIDATION, "OBJECT IS NULL");
        }

        SubjectDTO subjectDTO = (SubjectDTO) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "content", "content", "Content is not correct");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userDTO", "userDTO", "User is not correct");


    }
}
