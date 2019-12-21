package pl.com.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime dateTimeAnswer;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(cascade = CascadeType.PERSIST)
    private User user;

    private Integer positiveGrades;

    private Integer negativeGrades;

    @JoinColumn(name = "subject_id", nullable = false)
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Subject subject;
}
