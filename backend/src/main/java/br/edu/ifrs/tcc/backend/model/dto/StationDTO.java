package br.edu.ifrs.tcc.backend.model.dto;

import java.sql.Timestamp;

public record StationDTO(String name, Double latitude, Double longitude, String status, Timestamp installation,
        String project, String path_data_files, String acquirer_importer_cfg_str) {
}