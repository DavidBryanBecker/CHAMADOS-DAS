package br.com.sistemas.chamados.service;

import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.dto.ChamadoResponse;
import br.com.sistemas.chamados.entity.Categoria;
import br.com.sistemas.chamados.entity.Chamado;
import br.com.sistemas.chamados.entity.Cliente;
import br.com.sistemas.chamados.entity.StatusChamado;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.repository.CategoriaRepository;
import br.com.sistemas.chamados.repository.ChamadoRepository;
import br.com.sistemas.chamados.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChamadoService {

    private final ChamadoRepository repository;
    private final ClienteRepository clienteRepository;
    private final CategoriaRepository categoriaRepository;

    public ChamadoService(ChamadoRepository repository,
                          ClienteRepository clienteRepository,
                          CategoriaRepository categoriaRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public ChamadoResponse criar(ChamadoRequest dto) {
        Chamado chamado = new Chamado();
        preencher(chamado, dto);
        chamado.setStatus(StatusChamado.ABERTO);           // nasce ABERTO
        chamado.setDataAbertura(LocalDateTime.now());      // preenchida automaticamente
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
        preencher(chamado, dto);
        return ChamadoResponse.de(repository.save(chamado));
    }

    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Chamado buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new RecursoNaoEncontradoException("Chamado não encontrado: " + id));
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
