package br.com.sistemas.chamados.repository;

import br.com.sistemas.chamados.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ChamadoRepositoryTest {

    @Autowired private ChamadoRepository repository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private CategoriaRepository categoriaRepository;

    private Cliente ana;
    private Cliente bruno;
    private Categoria hardware;

    @BeforeEach
    void preparar() {
        ana = clienteRepository.save(cliente("Ana", "ana@exemplo.com"));
        bruno = clienteRepository.save(cliente("Bruno", "bruno@exemplo.com"));

        Categoria c = new Categoria();
        c.setNome("Hardware");
        hardware = categoriaRepository.save(c);

        repository.save(chamado("PC não liga", Prioridade.ALTA, StatusChamado.ABERTO, ana));
        repository.save(chamado("Mouse falhando", Prioridade.BAIXA, StatusChamado.EM_ATENDIMENTO, ana));
        repository.save(chamado("Teclado quebrado", Prioridade.ALTA, StatusChamado.ABERTO, bruno));
    }

    private Cliente cliente(String nome, String email) {
        Cliente c = new Cliente();
        c.setNome(nome);
        c.setEmail(email);
        return c;
    }

    private Chamado chamado(String titulo, Prioridade prioridade, StatusChamado status, Cliente cliente) {
        Chamado ch = new Chamado();
        ch.setTitulo(titulo);
        ch.setPrioridade(prioridade);
        ch.setStatus(status);
        ch.setDataAbertura(LocalDateTime.now());
        ch.setCliente(cliente);
        ch.setCategoria(hardware);
        return ch;
    }

    private Page<Chamado> filtrar(StatusChamado s, Prioridade p, Long clienteId) {
        return repository.filtrar(s, p, clienteId, PageRequest.of(0, 10));
    }

    @Test
    void semFiltrosRetornaTodos() {
        assertEquals(3, filtrar(null, null, null).getTotalElements());
    }

    @Test
    void filtraPorStatus() {
        assertEquals(2, filtrar(StatusChamado.ABERTO, null, null).getTotalElements());
    }

    @Test
    void filtraPorPrioridade() {
        assertEquals(2, filtrar(null, Prioridade.ALTA, null).getTotalElements());
    }

    @Test
    void filtraPorCliente() {
        assertEquals(1, filtrar(null, null, bruno.getId()).getTotalElements());
    }

    @Test
    void combinaOsTresFiltros() {
        assertEquals(1,
                filtrar(StatusChamado.ABERTO, Prioridade.ALTA, ana.getId()).getTotalElements());
    }
}
