package br.edu.ifrs.tcc.backend.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.cdimascio.dotenv.Dotenv;

public class CreateDataBase {

    private Dotenv dotenv = Dotenv.load();

    public CreateDataBase() {
    }

    private Connection getConnection() {
        String url = "jdbc:postgresql://" + dotenv.get("DB_HOST") + ":" + dotenv.get("DB_PORT") + "/";
        try {
            System.out.println("Conectado");
            return DriverManager.getConnection(url, dotenv.get("DB_USER"), dotenv.get("DB_PASSWORD"));
        } catch (SQLException ex) {
            System.out.println("Erro ao conectar");
            Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void createPmelDB() {
        try {
            System.out.println("Creating database if not exist...");
            Connection connection = this.getConnection();
            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT count(*) FROM pg_database WHERE datname = '" + dotenv.get("DB_NAME") + "'");
            ResultSet resultSet = statement.getResultSet();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count <= 0) {
                statement.executeUpdate("CREATE DATABASE " + dotenv.get("DB_NAME"));
                System.out.println("Database created.");
            } else {
                System.out.println("Database already exist.");
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
    }
}
