package pl.com.app.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.com.app.dto.AnswerDTO;
import pl.com.app.model.Answer;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    @Mappings({
            @Mapping(source = "user", target = "userDTO"),
            @Mapping(source = "subject", target = "subjectDTO"),
    })
    AnswerDTO answerToAnswerDTO(Answer answer);

    @Mappings({
            @Mapping(source = "userDTO", target = "user"),
            @Mapping(source = "subjectDTO", target = "subject"),
    })
    Answer answerDTOToAnswer(AnswerDTO answerDTO);
}

