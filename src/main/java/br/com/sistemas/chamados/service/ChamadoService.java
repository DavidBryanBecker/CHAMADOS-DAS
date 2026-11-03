package br.com.sistemas.chamados.service;

import br.com.sistemas.chamados.dto.AtribuirRequest;
import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.dto.ChamadoResponse;
import br.com.sistemas.chamados.dto.ComentarioRequest;
import br.com.sistemas.chamados.dto.ComentarioResponse;
import br.com.sistemas.chamados.dto.StatusRequest;
import br.com.sistemas.chamados.entity.Categoria;
import br.com.sistemas.chamados.entity.Chamado;
import br.com.sistemas.chamados.entity.Cliente;
import br.com.sistemas.chamados.entity.Comentario;
import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;
import br.com.sistemas.chamados.entity.Tecnico;
import br.com.sistemas.chamados.exception.ChamadoFechadoException;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.repository.CategoriaRepository;
import br.com.sistemas.chamados.repository.ChamadoRepository;
import br.com.sistemas.chamados.repository.ClienteRepository;
import br.com.sistemas.chamados.repository.ComentarioRepository;
import br.com.sistemas.chamados.repository.TecnicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ComentarioRepository comentarioRepository;

    public ChamadoService(ChamadoRepository repository,
                          ClienteRepository clienteRepository,
                          CategoriaRepository categoriaRepository,
                          TecnicoRepository tecnicoRepository,
                          ComentarioRepository comentarioRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.categoriaRepository = categoriaRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.comentarioRepository = comentarioRepository;
    }

    public ChamadoResponse criar(ChamadoRequest dto) {
        Chamado chamado = new Chamado();
        preencher(chamado, dto);
        chamado.setStatus(StatusChamado.ABERTO);
        chamado.setDataAbertura(LocalDateTime.now());
        return ChamadoResponse.de(repository.save(chamado));
    }

    public Page<ChamadoResponse> listar(StatusChamado status, Prioridade prioridade,
                                        Long clienteId, Pageable pageable) {
        return repository.filtrar(status, prioridade, clienteId, pageable)
                .map(ChamadoResponse::de);
    }

    public Page<ChamadoResponse> listarPorCliente(Long clienteId, Pageable pageable) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new RecursoNaoEncontradoException("Cliente não encontrado: " + clienteId);
        }
        return listar(null, null, clienteId, pageable);
    }

    public ChamadoResponse buscar(Long id) {
        Chamado chamado = buscarEntidade(id);
        return ChamadoResponse.de(chamado, listarComentarios(id));
    }

    public ChamadoResponse atualizar(Long id, ChamadoRequest dto) {
        Chamado chamado = buscarEntidade(id);
        garantirNaoFechado(chamado);
        preencher(chamado, dto);
        return ChamadoResponse.de(repository.save(chamado));
    }

    @Transactional
    public void excluir(Long id) {
        Chamado chamado = buscarEntidade(id);
        comentarioRepository.deleteByChamadoId(id);
        repository.delete(chamado);
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

    public ComentarioResponse comentar(Long chamadoId, ComentarioRequest dto) {
        Chamado chamado = buscarEntidade(chamadoId);
        Comentario comentario = new Comentario();
        comentario.setTexto(dto.texto());
        comentario.setAutor(dto.autor());
        comentario.setData(LocalDateTime.now());
        comentario.setChamado(chamado);
        return ComentarioResponse.de(comentarioRepository.save(comentario));
    }

    public List<ComentarioResponse> listarComentarios(Long chamadoId) {
        return comentarioRepository.findByChamadoIdOrderByDataAsc(chamadoId).stream()
                .map(ComentarioResponse::de)
                .toList();
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