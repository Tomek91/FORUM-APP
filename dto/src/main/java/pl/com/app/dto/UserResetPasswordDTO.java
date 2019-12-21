package pl.com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResetPasswordDTO {
    private Long id;
    private String password;
    private String passwordConfirmation;
    private String oldPassword;
}
