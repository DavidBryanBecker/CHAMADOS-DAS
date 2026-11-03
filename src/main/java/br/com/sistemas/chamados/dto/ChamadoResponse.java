package br.com.sistemas.chamados.dto;

import br.com.sistemas.chamados.entity.Chamado;
import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

public record ChamadoResponse(
        Long id,
        String titulo,
        String descricao,
        LocalDateTime dataAbertura,
        LocalDateTime dataFechamento,
        Prioridade prioridade,
        StatusChamado status,
        Long clienteId,
        String clienteNome,
        Long categoriaId,
        String categoriaNome,
        Long tecnicoId,
        String tecnicoNome,
        @JsonInclude(JsonInclude.Include.NON_NULL) List<ComentarioResponse> comentarios) {

    public static ChamadoResponse de(Chamado c) {
        return de(c, null);
    }

    public static ChamadoResponse de(Chamado c, List<ComentarioResponse> comentarios) {
        return new ChamadoResponse(
                c.getId(),
                c.getTitulo(),
                c.getDescricao(),
                c.getDataAbertura(),
                c.getDataFechamento(),
                c.getPrioridade(),
                c.getStatus(),
                c.getCliente().getId(),
                c.getCliente().getNome(),
                c.getCategoria().getId(),
                c.getCategoria().getNome(),
                c.getTecnico() == null ? null : c.getTecnico().getId(),
                c.getTecnico() == null ? null : c.getTecnico().getNome(),
                comentarios);
    }
}