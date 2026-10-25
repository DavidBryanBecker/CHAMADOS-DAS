package br.com.sistemas.chamados.service;

import br.com.sistemas.chamados.dto.AtribuirRequest;
import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.dto.ChamadoResponse;
import br.com.sistemas.chamados.dto.StatusRequest;
import br.com.sistemas.chamados.entity.Categoria;
import br.com.sistemas.chamados.entity.Chamado;
import br.com.sistemas.chamados.entity.Cliente;
import br.com.sistemas.chamados.entity.StatusChamado;
import br.com.sistemas.chamados.entity.Tecnico;
import br.com.sistemas.chamados.exception.ChamadoFechadoException;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.repository.CategoriaRepository;
import br.com.sistemas.chamados.repository.ChamadoRepository;
import br.com.sistemas.chamados.repository.ClienteRepository;
import br.com.sistemas.chamados.repository.TecnicoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ChamadoService {

    private static final Map<StatusChamado, Set<StatusChamado>> TRANSICOES = Map.of(
            StatusChamado.ABERTO,
            EnumSet.of(StatusChamado.EM_ATENDIMENTO, StatusChamado.FECHADO),
            StatusChamado.EM_ATENDIMENTO,
            EnumSet.of(StatusChamado.AGUARDANDO_CLIENTE, StatusChamado.FECHADO),
            StatusChamado.AGUARDANDO_CLIENTE,
            EnumSet.of(StatusChamado.EM_ATENDIMENTO, StatusChamado.FECHADO),
            StatusChamado.FECHADO,
            EnumSet.noneOf(StatusChamado.class));

    private final ChamadoRepository repository;
    private final ClienteRepository clienteRepository;
    private final CategoriaRepository categoriaRepository;
    private final TecnicoRepository tecnicoRepository;

    public ChamadoService(ChamadoRepository repository,
                          ClienteRepository clienteRepository,
                          CategoriaRepository categoriaRepository,
                          TecnicoRepository tecnicoRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.categoriaRepository = categoriaRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    public ChamadoResponse criar(ChamadoRequest dto) {
        Chamado chamado = new Chamado();
        preencher(chamado, dto);
        chamado.setStatus(StatusChamado.ABERTO);
        chamado.setDataAbertura(LocalDateTime.now());
        return ChamadoResponse.de(repository.save(chamado));
    }

    public List<ChamadoResponse> listar() {
        return repository.findAll().stream().map(ChamadoResponse::de).toList();
    }

    public ChamadoResponse buscar(Long id) {
        return ChamadoResponse.de(buscarEntidade(id));
    }

    public ChamadoResponse atualizar(Long id, ChamadoRequest dto) {
        Chamado chamado = buscarEntidade(id);
        garantirNaoFechado(chamado);
        preencher(chamado, dto);
        return ChamadoResponse.de(repository.save(chamado));
    }

    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    public ChamadoResponse atribuir(Long id, AtribuirRequest dto) {
        Chamado chamado = buscarEntidade(id);
        garantirNaoFechado(chamado);
        Tecnico tecnico = tecnicoRepository.findById(dto.tecnicoId()).orElseThrow(() ->
                new RecursoNaoEncontradoException("Técnico não encontrado: " + dto.tecnicoId()));
        chamado.setTecnico(tecnico);
        return ChamadoResponse.de(repository.save(chamado));
    }

    public ChamadoResponse alterarStatus(Long id, StatusRequest dto) {
        Chamado chamado = buscarEntidade(id);
        garantirNaoFechado(chamado);

        StatusChamado atual = chamado.getStatus();
        StatusChamado novo = dto.status();
        if (!TRANSICOES.get(atual).contains(novo)) {
            throw new RegraNegocioException("Transição inválida: " + atual + " -> " + novo);
        }

        chamado.setStatus(novo);
        if (novo == StatusChamado.FECHADO) {
            chamado.setDataFechamento(LocalDateTime.now());
        }
        return ChamadoResponse.de(repository.save(chamado));
    }

    private Chamado buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new RecursoNaoEncontradoException("Chamado não encontrado: " + id));
    }

    private void garantirNaoFechado(Chamado chamado) {
        if (chamado.getStatus() == StatusChamado.FECHADO) {
            throw new ChamadoFechadoException("Chamado fechado não pode ser alterado");
        }
    }

    private void preencher(Chamado chamado, ChamadoRequest dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId()).orElseThrow(() ->
                new RecursoNaoEncontradoException("Cliente não encontrado: " + dto.clienteId()));
        Categoria categoria = categoriaRepository.findById(dto.categoriaId()).orElseThrow(() ->
                new RecursoNaoEncontradoException("Categoria não encontrada: " + dto.categoriaId()));

        chamado.setTitulo(dto.titulo());
        chamado.setDescricao(dto.descricao());
        chamado.setPrioridade(dto.prioridade());
        chamado.setCliente(cliente);
        chamado.setCategoria(categoria);
    }
}