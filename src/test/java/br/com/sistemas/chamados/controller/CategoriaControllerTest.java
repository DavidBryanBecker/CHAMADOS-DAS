package br.com.sistemas.chamados.controller;

import br.com.sistemas.chamados.dto.CategoriaResponse;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService service;

    private static final String JSON_OK = "{\"nome\":\"Hardware\",\"descricao\":\"Equipamentos\"}";

    @Test
    void criaCategoriaRetorna201() throws Exception {
        when(service.criar(any())).thenReturn(new CategoriaResponse(1L, "Hardware", "Equipamentos"));

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void nomeVazioRetorna400() throws Exception {
        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void nomeDuplicadoRetorna409() throws Exception {
        when(service.criar(any())).thenThrow(new RegraNegocioException("nome duplicado"));

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
                .andExpect(status().isConflict());
    }

    @Test
    void idInexistenteRetorna404() throws Exception {
        when(service.buscar(anyLong())).thenThrow(new RecursoNaoEncontradoException("não achou"));

        mockMvc.perform(get("/categorias/999"))
                .andExpect(status().isNotFound());
    }
}
