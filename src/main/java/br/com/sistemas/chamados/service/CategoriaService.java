package br.com.sistemas.chamados.service;

import br.com.sistemas.chamados.dto.CategoriaRequest;
import br.com.sistemas.chamados.dto.CategoriaResponse;
import br.com.sistemas.chamados.entity.Categoria;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public CategoriaResponse criar(CategoriaRequest dto) {
        if (repository.existsByNomeIgnoreCase(dto.nome())) {
            throw new RegraNegocioException("Já existe uma categoria com este nome");
        }
        Categoria c = new Categoria();
        preencher(c, dto);
        return CategoriaResponse.de(repository.save(c));
    }

    public List<CategoriaResponse> listar() {
        return repository.findAll().stream().map(CategoriaResponse::de).toList();
    }

    public CategoriaResponse buscar(Long id) {
        return CategoriaResponse.de(buscarEntidade(id));
    }

    public CategoriaResponse atualizar(Long id, CategoriaRequest dto) {
        Categoria c = buscarEntidade(id);
        if (repository.existsByNomeIgnoreCaseAndIdNot(dto.nome(), id)) {
            throw new RegraNegocioException("Já existe uma categoria com este nome");
        }
        preencher(c, dto);
        return CategoriaResponse.de(repository.save(c));
    }

    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    public Categoria buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new RecursoNaoEncontradoException("Categoria não encontrada: " + id));
    }

    private void preencher(Categoria c, CategoriaRequest dto) {
        c.setNome(dto.nome());
        c.setDescricao(dto.descricao());
    }
}
