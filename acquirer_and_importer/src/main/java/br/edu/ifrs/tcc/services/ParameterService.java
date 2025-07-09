package br.edu.ifrs.tcc.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import br.edu.ifrs.tcc.aux.GetConnection;
import br.edu.ifrs.tcc.model.Parameter;

public class ParameterService {
    private static HashMap<String, Parameter> parameters = getAllParams();

    private static HashMap<String, Parameter> getAllParams() {
        HashMap<String, Parameter> parameters = new HashMap<>();

        try (Connection conn = new GetConnection().get()) {
            String sql = "SELECT * FROM parameter;";
            PreparedStatement prepQuerySQL = conn.prepareStatement(sql);
            ResultSet rs = prepQuerySQL.executeQuery();

            while (rs.next()) {
                Integer id_parameter = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String type_class = rs.getString("type_class");
                String unit = rs.getString("unit");
                String equipment = rs.getString("equipment");

                parameters.put(name,
                        new Parameter(id_parameter, name, description, type_class, unit, equipment));
            }

            rs.close();
            prepQuerySQL.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return parameters;
    }

    public Parameter getParameterByName(String name_parameter) {
        return ParameterService.parameters.get(name_parameter);
    }
}