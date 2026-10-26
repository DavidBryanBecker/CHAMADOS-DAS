package br.com.sistemas.chamados.service;

import br.com.sistemas.chamados.dto.TecnicoRequest;
import br.com.sistemas.chamados.dto.TecnicoResponse;
import br.com.sistemas.chamados.entity.Tecnico;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.repository.TecnicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TecnicoService {

    private final TecnicoRepository repository;

    public TecnicoService(TecnicoRepository repository) {
        this.repository = repository;
    }

    public TecnicoResponse criar(TecnicoRequest dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new RegraNegocioException("Já existe um técnico com este e-mail");
        }
        Tecnico t = new Tecnico();
        preencher(t, dto);
        return TecnicoResponse.de(repository.save(t));
    }

    public List<TecnicoResponse> listar() {
        return repository.findAll().stream().map(TecnicoResponse::de).toList();
    }

    public TecnicoResponse buscar(Long id) {
        return TecnicoResponse.de(buscarEntidade(id));
    }

    public TecnicoResponse atualizar(Long id, TecnicoRequest dto) {
        Tecnico t = buscarEntidade(id);
        if (repository.existsByEmailAndIdNot(dto.email(), id)) {
            throw new RegraNegocioException("Já existe um técnico com este e-mail");
        }
        preencher(t, dto);
        return TecnicoResponse.de(repository.save(t));
    }

    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    public Tecnico buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new RecursoNaoEncontradoException("Técnico não encontrado: " + id));
    }

    private void preencher(Tecnico t, TecnicoRequest dto) {
        t.setNome(dto.nome());
        t.setEmail(dto.email());
        t.setPerfil(dto.perfil());
    }
}
