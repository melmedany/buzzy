package io.buzzy.sso.registration.mapper;


import io.buzzy.common.messaging.model.SuccessfulRegistrationDTO;
import io.buzzy.sso.core.repository.entity.User;
import io.buzzy.sso.registration.controller.model.SignupRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "updated", ignore = true)
    User toUser(SignupRequest signupRequest);

    @Mapping(source = "id", target = "ssoId")
    SuccessfulRegistrationDTO toSuccessfulRegistrationDTO(User user);


}
