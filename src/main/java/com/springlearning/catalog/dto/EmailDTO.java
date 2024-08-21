package com.springlearning.catalog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailDTO {

    @NotBlank(message = "Campo Requerido")
    @Email(message = "Email Inválido")
    private String email;

    public EmailDTO() {
    }

    public EmailDTO(String email) {
        this.email = email;
    }

    public @NotBlank(message = "Campo Requerido") @Email(message = "Email Inválido") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Campo Requerido") @Email(message = "Email Inválido") String email) {
        this.email = email;
    }
}
