package br.com.sistemas.chamados.config;

import br.com.sistemas.chamados.entity.*;
import br.com.sistemas.chamados.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DadosIniciais implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final CategoriaRepository categoriaRepository;
    private final TecnicoRepository tecnicoRepository;
    private final ChamadoRepository chamadoRepository;
    private final ComentarioRepository comentarioRepository;

    public DadosIniciais(ClienteRepository clienteRepository,
                         CategoriaRepository categoriaRepository,
                         TecnicoRepository tecnicoRepository,
                         ChamadoRepository chamadoRepository,
                         ComentarioRepository comentarioRepository) {
        this.clienteRepository = clienteRepository;
        this.categoriaRepository = categoriaRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.chamadoRepository = chamadoRepository;
        this.comentarioRepository = comentarioRepository;
    }

    @Override
    public void run(String... args) {
        if (clienteRepository.count() > 0) {
            return;
        }

        Cliente ana = clienteRepository.save(cliente("Ana Souza", "ana@exemplo.com", "47999999999"));
        Cliente bruno = clienteRepository.save(cliente("Bruno Lima", "bruno@exemplo.com", "47988887777"));

        Categoria hardware = categoriaRepository.save(categoria("Hardware", "Equipamentos"));
        Categoria rede = categoriaRepository.save(categoria("Rede", "Internet e conectividade"));
        Categoria software = categoriaRepository.save(categoria("Software", "Sistemas e aplicativos"));

        Tecnico carlos = tecnicoRepository.save(tecnico("Carlos", "carlos@empresa.com", Perfil.TECNICO));
        Tecnico marina = tecnicoRepository.save(tecnico("Marina", "marina@empresa.com", Perfil.ADMIN));

        chamadoRepository.save(chamado("PC não liga", "Sem energia",
                Prioridade.ALTA, StatusChamado.ABERTO, ana, hardware, null));

        Chamado redeLenta = chamadoRepository.save(chamado("Rede lenta", "Internet caindo",
                Prioridade.CRITICA, StatusChamado.EM_ATENDIMENTO, ana, rede, carlos));

        chamadoRepository.save(chamado("Teclado quebrado", "Teclas travadas",
                Prioridade.ALTA, StatusChamado.AGUARDANDO_CLIENTE, bruno, hardware, carlos));

        Chamado fechado = chamado("Erro no sistema", "Tela travando ao salvar",
                Prioridade.BAIXA, StatusChamado.FECHADO, bruno, software, marina);
        fechado.setDataFechamento(LocalDateTime.now());
        fechado = chamadoRepository.save(fechado);

        comentarioRepository.save(comentario("Verificando o roteador", "Carlos", redeLenta));
        comentarioRepository.save(comentario("Cabo trocado, monitorando", "Carlos", redeLenta));
        comentarioRepository.save(comentario("Atualização aplicada, problema resolvido", "Marina", fechado));
    }

    private Cliente cliente(String nome, String email, String telefone) {
        Cliente c = new Cliente();
        c.setNome(nome);
        c.setEmail(email);
        c.setTelefone(telefone);
        return c;
    }

    private Categoria categoria(String nome, String descricao) {
        Categoria c = new Categoria();
        c.setNome(nome);
        c.setDescricao(descricao);
        return c;
    }

    private Tecnico tecnico(String nome, String email, Perfil perfil) {
        Tecnico t = new Tecnico();
        t.setNome(nome);
        t.setEmail(email);
        t.setPerfil(perfil);
        return t;
    }

    private Chamado chamado(String titulo, String descricao, Prioridade prioridade,
                            StatusChamado status, Cliente cliente, Categoria categoria,
                            Tecnico tecnico) {
        Chamado c = new Chamado();
        c.setTitulo(titulo);
        c.setDescricao(descricao);
        c.setPrioridade(prioridade);
        c.setStatus(status);
        c.setDataAbertura(LocalDateTime.now());
        c.setCliente(cliente);
        c.setCategoria(categoria);
        c.setTecnico(tecnico);
        return c;
    }

    private Comentario comentario(String texto, String autor, Chamado chamado) {
        Comentario c = new Comentario();
        c.setTexto(texto);
        c.setAutor(autor);
        c.setData(LocalDateTime.now());
        c.setChamado(chamado);
        return c;
    }
}
