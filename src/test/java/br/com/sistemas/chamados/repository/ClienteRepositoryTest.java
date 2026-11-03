package br.com.sistemas.chamados.repository;

import br.com.sistemas.chamados.entity.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository repository;

    private Cliente novo(String nome, String email) {
        Cliente c = new Cliente();
        c.setNome(nome);
        c.setEmail(email);
        c.setTelefone("47999999999");
        return c;
    }

    @Test
    void salvaEGeraId() {
        Cliente salvo = repository.save(novo("Ana Souza", "ana@exemplo.com"));
        assertNotNull(salvo.getId());
    }

    @Test
    void existsByEmailDetectaEmailCadastrado() {
        repository.save(novo("Ana Souza", "ana@exemplo.com"));
        assertTrue(repository.existsByEmail("ana@exemplo.com"));
        assertFalse(repository.existsByEmail("outro@exemplo.com"));
    }

    @Test
    void existsByEmailAndIdNotIgnoraOProprioRegistro() {
        Cliente ana = repository.save(novo("Ana Souza", "ana@exemplo.com"));
        assertFalse(repository.existsByEmailAndIdNot("ana@exemplo.com", ana.getId()));
        assertTrue(repository.existsByEmailAndIdNot("ana@exemplo.com", ana.getId() + 1));
    }

    @Test
    void bancoRejeitaEmailDuplicado() {
        repository.saveAndFlush(novo("Ana Souza", "ana@exemplo.com"));
        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(novo("Outra Ana", "ana@exemplo.com")));
    }
}
