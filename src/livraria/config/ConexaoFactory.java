package livraria.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConexaoFactory {

    private static final Properties props = new Properties();

    static {
        try (InputStream input = ConexaoFactory.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException(
                    "Arquivo config.properties não encontrado! " +
                    "Copie config.properties.example para config.properties " +
                    "e preencha com suas credenciais do MySQL."
                );
            }
            props.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar config.properties", e);
        }
    }

    public static Connection conectar() throws SQLException {
        String url   = props.getProperty("db.url");
        String user  = props.getProperty("db.user");
        String senha = props.getProperty("db.password");
        return DriverManager.getConnection(url, user, senha);
    }
}
