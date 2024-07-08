package io.buzzy.api.profile.mapper;

import io.buzzy.api.profile.controller.model.SettingsDTO;
import io.buzzy.api.profile.repository.entity.Settings;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SettingsMapper {
    Settings toEntity(SettingsDTO settingsDTO);

    SettingsDTO toDTO(Settings settingsDTO);
}
