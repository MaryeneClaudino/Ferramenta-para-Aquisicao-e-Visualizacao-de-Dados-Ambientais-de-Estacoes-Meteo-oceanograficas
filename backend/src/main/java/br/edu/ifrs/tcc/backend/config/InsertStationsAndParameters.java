package br.edu.ifrs.tcc.backend.config;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import br.edu.ifrs.tcc.backend.aux.Utils;
import br.edu.ifrs.tcc.backend.model.dto.ParameterDTO;
import br.edu.ifrs.tcc.backend.model.dto.StationDTO;
import br.edu.ifrs.tcc.backend.service.ParameterService;
import br.edu.ifrs.tcc.backend.service.StationService;

@Configuration
public class InsertStationsAndParameters {
    @Autowired
    private StationService stationService;
    @Autowired
    private ParameterService parameterService;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            if (stationService.count() == 0) {
                File infoStations = new File("info_estacoes.csv");
                ArrayList<String> lines = Utils.getFileLines(infoStations);
                String header = lines.remove(0);
                System.out.println(header);

                for (String line : lines) {
                    String[] spt = line.split(";");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date parsedDate = dateFormat.parse(spt[4]);
                    Timestamp timestamp = new Timestamp(parsedDate.getTime());

                    StationDTO station = new StationDTO(spt[0], Double.parseDouble(spt[1]), Double.parseDouble(spt[2]),
                            spt[3], timestamp, spt[5], spt[6], spt[7]);

                    stationService.save(station);
                }

                System.out.println("Estações criadas com sucesso!");
            }

            if (parameterService.count() == 0) {
                File infoStations = new File("info_param.csv");
                ArrayList<String> lines = Utils.getFileLines(infoStations);
                String header = lines.remove(0);
                System.out.println(header);

                for (String line : lines) {
                    String[] spt = line.split(";");
                    ParameterDTO parameter = new ParameterDTO(spt[0], spt[1], spt[2], spt[3],
                            spt[4]);

                    parameterService.save(parameter);
                }

                System.out.println("Parametros criados com sucesso!");
            }
        };
    }
}