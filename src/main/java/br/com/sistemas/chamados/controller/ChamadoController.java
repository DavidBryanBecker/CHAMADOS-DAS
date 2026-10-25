package br.com.sistemas.chamados.controller;
import br.com.sistemas.chamados.dto.AtribuirRequest;
import br.com.sistemas.chamados.dto.StatusRequest;
import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.dto.ChamadoResponse;
import br.com.sistemas.chamados.service.ChamadoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {

    private final ChamadoService service;

    public ChamadoController(ChamadoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChamadoResponse criar(@Valid @RequestBody ChamadoRequest dto) {
        return service.criar(dto);
    }

    @GetMapping
    public List<ChamadoResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public ChamadoResponse buscar(@PathVariable Long id) { return service.buscar(id); }

    @PutMapping("/{id}")
    public ChamadoResponse atualizar(@PathVariable Long id,
                                     @Valid @RequestBody ChamadoRequest dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) { service.excluir(id); }

        @PatchMapping("/{id}/atribuir")
    public ChamadoResponse atribuir(@PathVariable Long id,
                                    @Valid @RequestBody AtribuirRequest dto) {
        return service.atribuir(id, dto);
    }

    @PatchMapping("/{id}/status")
    public ChamadoResponse alterarStatus(@PathVariable Long id,
                                         @Valid @RequestBody StatusRequest dto) {
        return service.alterarStatus(id, dto);
    }
}