package livraria.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;


public class ConexaoFactory {

    private static final String ARQUIVO_CONFIG = "config.properties";
    private static final String SCRIPT_SETUP = "/livraria_setup.sql";

    private static Properties props;

    public static Connection conectar() throws SQLException {
        if (props == null) {
            carregarOuCriarConfig();
        }

        String url   = props.getProperty("db.url");
        String user  = props.getProperty("db.user");
        String senha = props.getProperty("db.password");

        try {
            return DriverManager.getConnection(url, user, senha);
        } catch (SQLException e) {
            
            if (e.getErrorCode() == 1049 || (e.getMessage() != null && e.getMessage().contains("Unknown database"))) {
                System.out.println("\nBanco de dados \"livraria\" não encontrado. Criando automaticamente...");
                criarBancoAutomaticamente(url, user, senha);
                System.out.println("Banco criado com sucesso!\n");
                return DriverManager.getConnection(url, user, senha);
            }
            throw e;
        }
    }

  
    private static void carregarOuCriarConfig() {
        props = new Properties();
        File arquivo = new File(ARQUIVO_CONFIG);

        if (arquivo.exists()) {
            try (InputStream input = new FileInputStream(arquivo)) {
                props.load(input);
                return;
            } catch (IOException e) {
                throw new RuntimeException("Erro ao ler " + ARQUIVO_CONFIG, e);
            }
        }

        System.out.println("===== Primeira execução: configuração do banco de dados =====");
        Scanner sc = new Scanner(System.in);

        System.out.print("Usuário do MySQL (Enter para usar 'root'): ");
        String user = sc.nextLine().trim();
        if (user.isEmpty()) {
            user = "root";
        }

        System.out.print("Senha do MySQL: ");
        String senha = sc.nextLine();

        String url = "jdbc:mysql://localhost:3306/livraria?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        props.setProperty("db.url", url);
        props.setProperty("db.user", user);
        props.setProperty("db.password", senha);

        try (OutputStream out = new FileOutputStream(arquivo)) {
            props.store(out, "Gerado automaticamente - NAO enviar para o Git");
            System.out.println("Configuração salva. Da próxima vez não será perguntado de novo.\n");
        } catch (IOException e) {
            System.out.println("Aviso: não foi possível salvar " + ARQUIVO_CONFIG + " (" + e.getMessage() + ")");
        }
    }

   
    private static void criarBancoAutomaticamente(String urlComBanco, String user, String senha) throws SQLException {
        // Remove o nome do banco da URL para conectar só no servidor
        String urlServidor = urlComBanco.replaceFirst("/livraria(\\?|$)", "/$1");

        try (Connection conn = DriverManager.getConnection(urlServidor, user, senha);
             InputStream sqlStream = ConexaoFactory.class.getResourceAsStream(SCRIPT_SETUP)) {

            if (sqlStream == null) {
                throw new RuntimeException(
                    "Não encontrei o arquivo livraria_setup.sql no projeto. " +
                    "Ele precisa estar na pasta src (mesmo nível dos pacotes)."
                );
            }

            String script = new String(sqlStream.readAllBytes(), StandardCharsets.UTF_8);
            executarScript(conn, script);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler livraria_setup.sql", e);
        }
    }

    
    private static void executarScript(Connection conn, String script) throws SQLException {
        String delimitador = ";";
        StringBuilder comandoAtual = new StringBuilder();

        for (String linha : script.split("\n")) {
            String linhaTrim = linha.trim();

            if (linhaTrim.isEmpty() || linhaTrim.startsWith("--")) {
                continue;
            }

            if (linhaTrim.toUpperCase().startsWith("DELIMITER")) {
                delimitador = linhaTrim.substring("DELIMITER".length()).trim();
                continue;
            }

            comandoAtual.append(linha).append("\n");

            String acumulado = comandoAtual.toString().trim();
            if (acumulado.endsWith(delimitador)) {
                String comando = acumulado.substring(0, acumulado.length() - delimitador.length()).trim();
                if (!comando.isEmpty()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(comando);
                    }
                }
                comandoAtual.setLength(0);
            }
        }
    }
}