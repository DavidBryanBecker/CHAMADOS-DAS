package br.com.sistemas.chamados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ComentarioForm {

    @NotBlank(message = "Texto é obrigatório")
    @Size(max = 1000, message = "Texto deve ter no máximo 1000 caracteres")
    private String texto;

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}