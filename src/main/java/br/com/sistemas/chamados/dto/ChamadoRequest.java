package br.com.sistemas.chamados.dto;

import br.com.sistemas.chamados.entity.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChamadoRequest(
        @NotBlank @Size(max = 150) String titulo,
        @Size(max = 1000) String descricao,
        @NotNull Prioridade prioridade,
        @NotNull Long clienteId,
        @NotNull Long categoriaId) {}
