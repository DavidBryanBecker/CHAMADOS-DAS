package br.com.sistemas.chamados.controller;

import br.com.sistemas.chamados.dto.ClienteForm;
import br.com.sistemas.chamados.dto.ClienteRequest;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.ClienteService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Hidden
@Controller
@RequestMapping("/app/clientes")
public class ClientePaginaController {

    private final ClienteService service;

    public ClientePaginaController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("clientes", service.listar());
        return "clientes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("clienteForm", new ClienteForm());
        return "clientes/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("clienteForm") ClienteForm form,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "clientes/form";
        }
        try {
            service.criar(new ClienteRequest(form.getNome(), form.getEmail(), form.getTelefone()));
        } catch (RegraNegocioException e) {
            result.rejectValue("email", "duplicado", e.getMessage());
            return "clientes/form";
        }
        redirect.addFlashAttribute("sucesso", "Cliente cadastrado com sucesso.");
        return "redirect:/app/clientes";
    }
}