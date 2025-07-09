package br.edu.ifrs.tcc.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import br.edu.ifrs.tcc.aux.GetConnection;
import br.edu.ifrs.tcc.model.Station;
import br.edu.ifrs.tcc.services.exceptions.NotFoundException;

public class StationService {
    public Station getStationByName(String nameStation) throws SQLException {
        String sql = "SELECT * FROM station WHERE name = ?";
        Station station = null;
        try (Connection conn = new GetConnection().get()) {
            PreparedStatement prepQuerySQL = conn.prepareStatement(sql);
            prepQuerySQL.setString(1, nameStation);
            ResultSet rs = prepQuerySQL.executeQuery();
            if (rs.next()) {
                Integer id_station = (rs.getInt("id"));
                String name = (rs.getString("name"));
                Double lat = (rs.getDouble("latitude"));
                Double lng = (rs.getDouble("longitude"));
                String status = (rs.getString("status"));
                Timestamp installation = (rs.getTimestamp("installation"));
                String project = (rs.getString("project"));
                String path = (rs.getString("path_data_files"));
                String acquirer_importer_cfg_str = (rs.getString("acquirer_importer_cfg"));

                station = new Station(id_station, name, lat, lng, status, installation, project, path,
                        acquirer_importer_cfg_str);
            }
            prepQuerySQL.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (station == null) {
            throw new NotFoundException("Estação (" + nameStation + ") não encontrada.");
        }

        return station;
    }
}