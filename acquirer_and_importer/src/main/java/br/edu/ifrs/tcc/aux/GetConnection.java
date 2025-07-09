package br.edu.ifrs.tcc.aux;

import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

public class GetConnection {

    public String user;
    public String pass;
    private Dotenv dotenv = Dotenv.load();

    public GetConnection() {
        try {
            user = dotenv.get("DB_USER");
            pass = dotenv.get("DB_PASSWORD");
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
            user = "invalid";
            pass = "invalid";
        }
    }

    public Connection get() throws ClassNotFoundException, SQLException {
        String USUARIO = user;
        String SENHA = pass;
        String URL = "jdbc:postgresql://" + dotenv.get("DB_HOST") + ":" + dotenv.get("DB_PORT") + "/"
                + dotenv.get("DB_NAME");

        // System.out.println(URL);
        Connection conn = DriverManager.getConnection(URL, USUARIO, SENHA);

        return conn;
    }
}