package br.com.sistemas.chamados.controller;

import br.com.sistemas.chamados.dto.ChamadoForm;
import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.CategoriaService;
import br.com.sistemas.chamados.service.ChamadoService;
import br.com.sistemas.chamados.service.ClienteService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Hidden
@Controller
@RequestMapping("/app/chamados")
public class ChamadoPaginaController {

    private final ChamadoService chamadoService;
    private final ClienteService clienteService;
    private final CategoriaService categoriaService;

    public ChamadoPaginaController(ChamadoService chamadoService,
                                   ClienteService clienteService,
                                   CategoriaService categoriaService) {
        this.chamadoService = chamadoService;
        this.clienteService = clienteService;
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) StatusChamado status,
                         @RequestParam(required = false) Prioridade prioridade,
                         @RequestParam(required = false) Long clienteId,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("dataAbertura").descending());
        model.addAttribute("pagina",
                chamadoService.listar(status, prioridade, clienteId, pageable));
        model.addAttribute("statusLista", StatusChamado.values());
        model.addAttribute("prioridades", Prioridade.values());
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("filtroStatus", status);
        model.addAttribute("filtroPrioridade", prioridade);
        model.addAttribute("filtroClienteId", clienteId);
        return "chamados/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("chamadoForm", new ChamadoForm());
        carregarListas(model);
        return "chamados/form";
    }

    @PostMapping
    public String salvar(@Valid ChamadoForm chamadoForm,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            carregarListas(model);
            return "chamados/form";
        }
        try {
            chamadoService.criar(new ChamadoRequest(
                    chamadoForm.getTitulo(),
                    chamadoForm.getDescricao(),
                    chamadoForm.getPrioridade(),
                    chamadoForm.getClienteId(),
                    chamadoForm.getCategoriaId()));
        } catch (RecursoNaoEncontradoException | RegraNegocioException e) {
            result.reject("erroNegocio", e.getMessage());
            carregarListas(model);
            return "chamados/form";
        }
        redirect.addFlashAttribute("sucesso", "Chamado aberto com sucesso!");
        return "redirect:/app/chamados";
    }

    private void carregarListas(Model model) {
        model.addAttribute("prioridades", Prioridade.values());
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("categorias", categoriaService.listar());
    }
}
