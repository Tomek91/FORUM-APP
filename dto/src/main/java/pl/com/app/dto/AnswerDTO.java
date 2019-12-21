package pl.com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerDTO {
    private Long id;
    private String content;
    private LocalDateTime dateTimeAnswer;
    private UserDTO userDTO;
    private Integer positiveGrades;
    private Integer negativeGrades;
    private SubjectDTO subjectDTO;
}
