package ai.uniauth.model.mapper;

import ai.uniauth.model.entity.UniSystem;
import ai.uniauth.model.dto.UniSystemDTO;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UniSystemDTO toDTO(UniSystem system);
}