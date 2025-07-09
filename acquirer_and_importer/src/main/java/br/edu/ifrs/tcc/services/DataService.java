package br.edu.ifrs.tcc.services;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import br.edu.ifrs.tcc.aux.GetConnection;
import br.edu.ifrs.tcc.model.Data;
import java.sql.Connection;

public class DataService {
    public void insert(Data data) throws SQLException {
        String sql = "INSERT INTO data (id_station, id_parameter, timestamp, value) VALUES (?,?,?,?);";
        try (Connection conn = new GetConnection().get()) {

            PreparedStatement prepQuerySQL = conn.prepareStatement(sql);
            prepQuerySQL.setInt(1, data.id_station);
            prepQuerySQL.setInt(2, data.id_parameter);
            prepQuerySQL.setTimestamp(3, data.timestamp);
            prepQuerySQL.setDouble(4, data.value);

            prepQuerySQL.execute();
            prepQuerySQL.close();
            conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}