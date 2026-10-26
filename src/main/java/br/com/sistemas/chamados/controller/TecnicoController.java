package br.com.sistemas.chamados.controller;

import br.com.sistemas.chamados.dto.TecnicoRequest;
import br.com.sistemas.chamados.dto.TecnicoResponse;
import br.com.sistemas.chamados.service.TecnicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tecnicos")
public class TecnicoController {

    private final TecnicoService service;

    public TecnicoController(TecnicoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TecnicoResponse criar(@Valid @RequestBody TecnicoRequest dto) {
        return service.criar(dto);
    }

    @GetMapping
    public List<TecnicoResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public TecnicoResponse buscar(@PathVariable Long id) { return service.buscar(id); }

    @PutMapping("/{id}")
    public TecnicoResponse atualizar(@PathVariable Long id,
                                     @Valid @RequestBody TecnicoRequest dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) { service.excluir(id); }
}