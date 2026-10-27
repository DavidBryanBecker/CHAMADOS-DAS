package br.com.sistemas.chamados.repository;

import br.com.sistemas.chamados.entity.Categoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository repository;

    private Categoria nova(String nome) {
        Categoria c = new Categoria();
        c.setNome(nome);
        return c;
    }

    @Test
    void salvaEGeraId() {
        assertNotNull(repository.save(nova("Hardware")).getId());
    }

    @Test
    void detectaNomeDuplicadoIgnorandoMaiusculas() {
        repository.save(nova("Hardware"));
        assertTrue(repository.existsByNomeIgnoreCase("hardware"));
        assertFalse(repository.existsByNomeIgnoreCase("Rede"));
    }
}