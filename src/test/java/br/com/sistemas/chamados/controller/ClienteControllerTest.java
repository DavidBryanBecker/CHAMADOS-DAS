package br.com.sistemas.chamados.controller;

import br.com.sistemas.chamados.dto.ClienteResponse;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.ChamadoService;
import br.com.sistemas.chamados.service.ClienteService;
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

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService service;

    @MockitoBean
    private ChamadoService chamadoService;

    private static final String JSON_OK =
            "{\"nome\":\"Ana Souza\",\"email\":\"ana@exemplo.com\",\"telefone\":\"47999999999\"}";

    @Test
    void criaClienteRetorna201() throws Exception {
        when(service.criar(any()))
                .thenReturn(new ClienteResponse(1L, "Ana Souza", "ana@exemplo.com", "47999999999"));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Ana Souza"));
    }

    @Test
    void nomeVazioRetorna400() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"email\":\"ana@exemplo.com\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void emailInvalidoRetorna400() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ana\",\"email\":\"invalido\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void emailDuplicadoRetorna409() throws Exception {
        when(service.criar(any())).thenThrow(new RegraNegocioException("e-mail duplicado"));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
                .andExpect(status().isConflict());
    }

    @Test
    void idInexistenteRetorna404() throws Exception {
        when(service.buscar(anyLong())).thenThrow(new RecursoNaoEncontradoException("não achou"));

        mockMvc.perform(get("/clientes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void excluirRetorna204() throws Exception {
        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());
    }
}
