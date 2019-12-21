package pl.com.app.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import pl.com.app.dto.UserDTO;
import pl.com.app.dto.UserResetPasswordDTO;
import pl.com.app.exceptions.ExceptionCode;
import pl.com.app.exceptions.MyException;
import pl.com.app.service.UserService;

@Component
@RequiredArgsConstructor
public class PasswordsValidator implements Validator {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UserResetPasswordDTO.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (o == null) {
            throw new MyException(ExceptionCode.VALIDATION, "OBJECT IS NULL");
        }

        UserResetPasswordDTO userResetPasswordDTO = (UserResetPasswordDTO) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password", "Password is not correct");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "oldPassword", "oldPassword", "Old password is not correct");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirmation", "passwordConfirmation", "Password confirmation is not correct");

        if (!errors.hasErrors()) {
            if (!userResetPasswordDTO.getPassword().equals(userResetPasswordDTO.getPasswordConfirmation())) {
                errors.reject("passwords_not_same", "PASSWORDS ARE NOT THE SAME");
            }
            if (userResetPasswordDTO.getPassword().equals(userResetPasswordDTO.getOldPassword())) {
                errors.reject("passwords_same", "NEW PASSWORD HAVE TO BE DIFFERENT THAN OLD PASSWORD");
            }
            if (userResetPasswordDTO.getId() != null) {
                UserDTO user = userService.getOneUser(userResetPasswordDTO.getId());
                if (!passwordEncoder.matches(userResetPasswordDTO.getOldPassword(), user.getPassword())){
                    errors.reject("password_old_wrong", "OLD PASSWORD IS WRONG");
                }
            }
        }
    }
}
