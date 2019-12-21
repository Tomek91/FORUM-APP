package pl.com.app.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.com.app.dto.SubjectDTO;
import pl.com.app.model.Subject;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
    @Mappings({
            @Mapping(source = "user", target = "userDTO")
    })
    SubjectDTO subjectToSubjectDTO(Subject subject);

    @Mappings({
            @Mapping(source = "userDTO", target = "user")
    })
    Subject subjectDTOToSubject(SubjectDTO subjectDTO);
}

