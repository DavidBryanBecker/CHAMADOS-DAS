package br.com.sistemas.chamados.controller;

import br.com.sistemas.chamados.dto.CategoriaForm;
import br.com.sistemas.chamados.dto.CategoriaRequest;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.CategoriaService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Hidden
@Controller
@RequestMapping("/app/categorias")
public class CategoriaPaginaController {

    private final CategoriaService service;

    public CategoriaPaginaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", service.listar());
        return "categorias/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("categoriaForm", new CategoriaForm());
        return "categorias/form";
    }

    @PostMapping
    public String salvar(@Valid CategoriaForm categoriaForm,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "categorias/form";
        }
        try {
            service.criar(new CategoriaRequest(
                    categoriaForm.getNome(),
                    categoriaForm.getDescricao()));
        } catch (RegraNegocioException e) {
            result.rejectValue("nome", "duplicado", e.getMessage());
            return "categorias/form";
        }
        redirect.addFlashAttribute("sucesso", "Categoria cadastrada com sucesso!");
        return "redirect:/app/categorias";
    }
}