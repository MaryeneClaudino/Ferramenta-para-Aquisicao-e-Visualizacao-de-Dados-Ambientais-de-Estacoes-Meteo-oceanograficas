package br.edu.ifrs.tcc.backend.repository;

import org.springframework.stereotype.Repository;
import br.edu.ifrs.tcc.backend.model.Data;
import br.edu.ifrs.tcc.backend.model.DataKey;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface DataRepository extends JpaRepository<Data, DataKey> {

    @Query(value = "SELECT DISTINCT(id_parameter) FROM data d WHERE d.id_station in (?1)", nativeQuery = true)
    List<Integer> getAllIdParamsStation(Integer id_station);

    @Query(value = "SELECT * FROM data d WHERE d.id_station in (?1) AND d.id_parameter in (?2) and date(d.timestamp) = ?3", nativeQuery = true)
    List<Data> getData(ArrayList<Integer> id_stations, ArrayList<Integer> id_parameters, LocalDate date);

    @Query(value = "SELECT * FROM data d WHERE d.id_station in (?1) AND d.id_parameter in (?2) and d.timestamp >= ?3 and d.timestamp <= ?4", nativeQuery = true)
    List<Data> getData(ArrayList<Integer> id_stations, ArrayList<Integer> id_parameters, Timestamp t1, Timestamp t2);

    @Query(value = "SELECT * FROM data d WHERE d.id_station = ?1 AND d.id_parameter in (?2) and d.timestamp >= ?3 and d.timestamp <= ?4", nativeQuery = true)
    List<Data> getData(Integer id_station, ArrayList<Integer> id_parameters, Timestamp t1, Timestamp t2);
}