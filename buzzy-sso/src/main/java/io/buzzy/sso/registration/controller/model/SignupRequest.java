package io.buzzy.sso.registration.controller.model;

import io.buzzy.sso.registration.validation.NoWhitespace;
import io.buzzy.sso.registration.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(@NotBlank(message = "validation.field.blank")
                            @Size(min = 3, max = 100, message = "validation.name.error")
                            String firstname,
                            @NotBlank(message = "validation.field.blank")
                            @Size(min = 3, max = 100, message = "validation.name.error")
                            String lastname,
                            @NotBlank(message = "validation.field.blank")
                            @NoWhitespace(message = "validation.username.no.whitspace")
                            @Size(min = 3, max = 50, message = "validation.username.error")
                            String username,
                            @NotBlank(message = "validation.field.blank")
                            @ValidPassword String password) {
}