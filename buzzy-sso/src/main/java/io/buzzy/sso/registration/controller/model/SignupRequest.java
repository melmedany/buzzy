package io.buzzy.sso.registration.controller.model;

import io.buzzy.sso.registration.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {
    @NotBlank(message = "validation.field.blank")
    @Size(min = 3, max = 100, message = "validation.name.error")
    private String firstname;
    @NotBlank(message = "validation.field.blank")
    @Size(min = 3, max = 100, message = "validation.name.error")
    private String lastname;
    @NotBlank(message = "validation.field.blank")
    @Size(min = 3, max = 50, message = "validation.username.error")
    private String username;
    @NotBlank(message = "validation.field.blank")
    @ValidPassword
    private String password;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}