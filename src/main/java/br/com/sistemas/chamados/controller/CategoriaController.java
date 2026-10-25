package br.com.sistemas.chamados.controller;

import br.com.sistemas.chamados.dto.CategoriaRequest;
import br.com.sistemas.chamados.dto.CategoriaResponse;
import br.com.sistemas.chamados.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponse criar(@Valid @RequestBody CategoriaRequest dto) {
        return service.criar(dto);
    }

    @GetMapping
    public List<CategoriaResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public CategoriaResponse buscar(@PathVariable Long id) { return service.buscar(id); }

    @PutMapping("/{id}")
    public CategoriaResponse atualizar(@PathVariable Long id,
                                       @Valid @RequestBody CategoriaRequest dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) { service.excluir(id); }
}