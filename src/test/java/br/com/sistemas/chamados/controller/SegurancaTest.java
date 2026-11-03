package br.com.sistemas.chamados.controller;

import br.com.sistemas.chamados.config.SecurityConfig;
import br.com.sistemas.chamados.service.CategoriaService;
import br.com.sistemas.chamados.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({DashboardController.class, CategoriaController.class})
@Import(SecurityConfig.class)
class SegurancaTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private CategoriaService categoriaService;

    @Test
    void semLoginRetorna401() throws Exception {
        mockMvc.perform(get("/dashboard")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TECNICO")
    void tecnicoNaoPodeDeletar() throws Exception {
        mockMvc.perform(delete("/categorias/1")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminAcessaDashboard() throws Exception {
        mockMvc.perform(get("/dashboard")).andExpect(status().isOk());
    }
}
