package br.edu.ifrs.tcc.model;

import java.sql.Timestamp;
import org.json.JSONObject;

public class Station {
    public Integer id;
    public String name;
    public Double latitude;
    public Double longitude;
    public String status;
    public Timestamp installation;
    public String project;
    public String path_data_files;
    public JSONObject acquirer_importer_cfg;

    public Station(Integer id, String name, Double latitude, Double longitude, String status,
            Timestamp installation, String project, String path_data_files, String acquirer_importer_cfg_str) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.installation = installation;
        this.project = project;
        this.path_data_files = path_data_files;
        if (acquirer_importer_cfg_str != null && !acquirer_importer_cfg_str.trim().isEmpty()) {
            this.acquirer_importer_cfg = new JSONObject(acquirer_importer_cfg_str);
        } else {
            this.acquirer_importer_cfg = null;
        }
    }
}