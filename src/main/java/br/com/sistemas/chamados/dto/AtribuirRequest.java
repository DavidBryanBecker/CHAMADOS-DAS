package br.com.sistemas.chamados.dto;

import jakarta.validation.constraints.NotNull;

public record AtribuirRequest(@NotNull Long tecnicoId) {}
