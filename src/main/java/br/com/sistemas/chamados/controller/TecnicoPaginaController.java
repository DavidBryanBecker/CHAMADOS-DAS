package br.com.sistemas.chamados.controller;

import br.com.sistemas.chamados.dto.TecnicoForm;
import br.com.sistemas.chamados.dto.TecnicoRequest;
import br.com.sistemas.chamados.entity.Perfil;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.TecnicoService;
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
@RequestMapping("/app/tecnicos")
public class TecnicoPaginaController {

    private final TecnicoService service;

    public TecnicoPaginaController(TecnicoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tecnicos", service.listar());
        return "tecnicos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("tecnicoForm", new TecnicoForm());
        model.addAttribute("perfis", Perfil.values());
        return "tecnicos/form";
    }

    @PostMapping
    public String salvar(@Valid TecnicoForm tecnicoForm,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("perfis", Perfil.values());
            return "tecnicos/form";
        }
        try {
            service.criar(new TecnicoRequest(
                    tecnicoForm.getNome(),
                    tecnicoForm.getEmail(),
                    tecnicoForm.getPerfil()));
        } catch (RegraNegocioException e) {
            result.rejectValue("email", "duplicado", e.getMessage());
            model.addAttribute("perfis", Perfil.values());
            return "tecnicos/form";
        }
        redirect.addFlashAttribute("sucesso", "Técnico cadastrado com sucesso!");
        return "redirect:/app/tecnicos";
    }
}
