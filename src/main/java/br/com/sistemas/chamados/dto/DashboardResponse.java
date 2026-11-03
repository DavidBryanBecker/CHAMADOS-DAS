package br.com.sistemas.chamados.dto;

import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;

import java.util.Map;

public record DashboardResponse(
        long totalChamados,
        Map<StatusChamado, Long> porStatus,
        Map<Prioridade, Long> porPrioridade) {}