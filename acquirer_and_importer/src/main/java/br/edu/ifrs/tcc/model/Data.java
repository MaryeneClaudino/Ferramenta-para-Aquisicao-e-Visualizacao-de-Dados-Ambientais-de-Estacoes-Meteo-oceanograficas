package br.edu.ifrs.tcc.model;

import java.sql.Timestamp;

public class Data {
    public Integer id_station;
    public Integer id_parameter;
    public Timestamp timestamp;
    public Double value;

    public Data(Integer id_station, Integer id_parameter, Timestamp timestamp, Double value) {
        this.id_station = id_station;
        this.id_parameter = id_parameter;
        this.timestamp = timestamp;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Data [id_station=" + id_station + ", id_parameter=" + id_parameter + ", timestamp=" + timestamp
                + ", value=" + value + "]";
    }
}