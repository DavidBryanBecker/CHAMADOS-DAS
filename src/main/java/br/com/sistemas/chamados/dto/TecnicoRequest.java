package br.com.sistemas.chamados.dto;

import br.com.sistemas.chamados.entity.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TecnicoRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Email @Size(max = 120) String email,
        @NotNull Perfil perfil) {}
