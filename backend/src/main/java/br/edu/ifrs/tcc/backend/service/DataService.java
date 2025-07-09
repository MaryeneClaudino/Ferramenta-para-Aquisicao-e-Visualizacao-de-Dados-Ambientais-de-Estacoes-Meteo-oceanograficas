package br.edu.ifrs.tcc.backend.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.edu.ifrs.tcc.backend.aux.Utils;
import br.edu.ifrs.tcc.backend.model.Data;
import br.edu.ifrs.tcc.backend.model.DataKey;
import br.edu.ifrs.tcc.backend.model.Parameter;
import br.edu.ifrs.tcc.backend.model.Station;
import br.edu.ifrs.tcc.backend.repository.DataRepository;
import br.edu.ifrs.tcc.backend.service.exceptions.NotFoundException;

@Service
public class DataService {

    @Autowired
    private DataRepository dataRepository;
    @Autowired
    private StationService stationService;
    @Autowired
    private ParameterService parameterService;

    public List<Data> getAll() {
        return dataRepository.findAll();
    }

    public List<Data> getData(ArrayList<Integer> id_stations, ArrayList<Integer> id_parameters, Timestamp t1,
            Timestamp t2) {
        return dataRepository.getData(id_stations, id_parameters, t1, t2);
    }

    public List<Data> getData(ArrayList<Integer> id_stations, ArrayList<Integer> id_parameters, LocalDate date) {
        return dataRepository.getData(id_stations, id_parameters, date);
    }

    public List<Data> getData(Integer id_station, ArrayList<Integer> id_parameters, Timestamp t1, Timestamp t2) {
        return dataRepository.getData(id_station, id_parameters, t1, t2);
    }

    public ArrayList<String> downloadData(Integer id_station, ArrayList<Integer> id_parameters, Timestamp t1,
            Timestamp t2) {
        ArrayList<String> fileLines = new ArrayList<>();

        Station station = stationService.getById(id_station);

        List<Data> allData = getData(id_station, id_parameters, t1, t2);
        if (allData.size() > 0) {
            fileLines.add("Projeto: " + station.getProject() + "     Estação: " + station.getName() + "     Latitude: "
                    + station.getLatitude() + "     Longitude: " + station.getLongitude() + "     Intalação: "
                    + station.getInstallation());

            String header = "Timestamp;";
            for (int i = 0; i < id_parameters.size(); i++) {
                System.out.println(id_parameters.get(i));
                Parameter param = parameterService.getById(id_parameters.get(i));
                fileLines
                        .add("Parâmetro: " + param.getName() + "     Descrição: " + param.getDescription()
                                + "     Classe: "
                                + param.getType_class().toUpperCase() + "     Equipamento: " + param.getEquipment());
                if (i == id_parameters.size() - 1) {
                    header += param.getName();
                } else {
                    header += param.getName() + ";";
                }
            }
            fileLines.add(header);
            ArrayList<Timestamp> allTimestamps = Utils.getAllDataTimestamp(allData);
            String[] headerSpt = header.split(";");

            for (Timestamp timestamp : allTimestamps) {
                String[] line = new String[headerSpt.length];
                line[0] = timestamp.toString();
                for (Data data : allData) {
                    if (data.getTimestamp().equals(timestamp)) {
                        for (int h = 0; h < headerSpt.length; h++) {
                            if (headerSpt[h].equals(data.getParameter().getName())) {
                                line[h] = data.getValue().toString();
                            }
                        }
                    }
                }

                fileLines.add(Utils.getLine(line));
            }

        }

        return fileLines;
    }

    public Data getById(Integer id_station, Integer id_parameter, Timestamp timestamp) {
        Station station = stationService.getById(id_station);
        Parameter param = parameterService.getById(id_parameter);
        DataKey id = new DataKey(station, param, timestamp);
        return dataRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("O Dado (" + id + ") não foi encontrado."));
    }

    public List<Integer> getAllIdParamsStation(Integer id_station) {
        return dataRepository.getAllIdParamsStation(id_station);
    }
}