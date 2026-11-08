package br.com.sistemas.chamados.controller;

import br.com.sistemas.chamados.service.DashboardService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Hidden
@Controller
public class PaginaController {

    private final DashboardService dashboardService;

    public PaginaController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/app";
    }

    @GetMapping("/app")
    public String dashboard(Model model) {
        model.addAttribute("resumo", dashboardService.resumo());
        return "dashboard";
    }
}
