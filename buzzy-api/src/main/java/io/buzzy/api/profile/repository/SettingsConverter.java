package io.buzzy.api.profile.repository;

import io.buzzy.api.profile.repository.entity.Settings;
import io.buzzy.common.util.JsonUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SettingsConverter implements AttributeConverter<Settings, String> {

    @Override
    public String convertToDatabaseColumn(Settings settings) {
        return JsonUtil.toJson(settings);
    }

    @Override
    public Settings convertToEntityAttribute(String settings) {
        return JsonUtil.fromJson(settings, Settings.class);
    }
}