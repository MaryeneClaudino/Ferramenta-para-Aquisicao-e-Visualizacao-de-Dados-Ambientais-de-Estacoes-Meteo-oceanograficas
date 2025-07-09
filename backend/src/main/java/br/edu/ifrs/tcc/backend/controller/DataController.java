package br.edu.ifrs.tcc.backend.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.edu.ifrs.tcc.backend.aux.Utils;
import br.edu.ifrs.tcc.backend.model.Data;
import br.edu.ifrs.tcc.backend.model.Station;
import br.edu.ifrs.tcc.backend.service.DataService;
import br.edu.ifrs.tcc.backend.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Dados das Estações De Monitoramento", description = "APIs de acesso aos dados das estações")
@RestController
@RequestMapping("/data")
public class DataController {

        @Autowired
        protected DataService dataService;
        @Autowired
        protected StationService stationService;

        @Operation(summary = "Informações dos dados das estações correspondente as variáveis informadas considerando um timestamp", description = "Retorna um JSON que contém as informações dos dados das estações referente as variáveis informadas considerando um timestamp")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Informações do dado da estação", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Dado da estação não encontrado com variáveis informadas", content = @Content)
        })
        @GetMapping("/{id_stations}/{id_parameters}/{date}")
        public ResponseEntity<List<Data>> getData(@PathVariable ArrayList<Integer> id_stations,
                        @PathVariable ArrayList<Integer> id_parameters, @PathVariable LocalDate date) {
                List<Data> data = dataService.getData(id_stations, id_parameters, date);
                return ResponseEntity.ok().body(data);
        }

        @Operation(summary = "Informações dos dados das estações correspondente as variáveis informadas considerando um intervalo de tempo", description = "Retorna um JSON que contém as informações dos dados das estações referente as variáveis informadas um intervalo de tempo")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Informações do dado da estação", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Dado da estação não encontrado com variáveis informadas", content = @Content)
        })
        @GetMapping("/{id_stations}/{id_parameters}/{t1}/{t2}")
        public ResponseEntity<List<Data>> getData(@PathVariable ArrayList<Integer> id_stations,
                        @PathVariable ArrayList<Integer> id_parameters, @PathVariable Timestamp t1,
                        @PathVariable Timestamp t2) {
                List<Data> data = dataService.getData(id_stations, id_parameters, t1, t2);
                return ResponseEntity.ok().body(data);
        }

        @Operation(summary = "Download das informações dos dados da estação, para os parâmetros informados e considerando um intervalo de tempo", description = "Arquivo .csv que contém as informações do dado da estação, para os parâmetros informados em um intervalo de tempo")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Arquivo contendo as informações com os dados da estação", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Dado da estação não encontrado para os parâmetros informados", content = @Content)
        })
        @GetMapping("/download/{id_station}/{id_parameters}/{t1}/{t2}")
        public ResponseEntity<Resource> downloadData(@PathVariable Integer id_station,
                        @PathVariable ArrayList<Integer> id_parameters, @PathVariable Timestamp t1,
                        @PathVariable Timestamp t2)
                        throws IOException {

                ArrayList<String> fileLines = dataService.downloadData(id_station, id_parameters, t1, t2);
                if (fileLines.size() > 0) {
                        Station station = stationService.getById(id_station);
                        File file = new File(station.getProject() + "_" + station.getName() + "_" + LocalDate.now()
                                        + ".csv");
                        Utils.writeLinesToFile(file, fileLines, true);

                        HttpHeaders header = new HttpHeaders();
                        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

                        Path path = Paths.get(file.getAbsolutePath());
                        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                        file.delete();

                        return ResponseEntity.ok()
                                        .headers(header)
                                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                        .body(resource);
                }
                return ResponseEntity.status(404)
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .body(null);
        }

        @Operation(summary = "Informações de todos os ids dos parâmetros vinculados à estação correspondente ao id da estação informado", description = "Retorna um JSON que contém as informações de todos os ids dos parâmetros vinculados à estação referente ao id da estação informado")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Informações de todos os ids dos parâmetros vinculados à estação", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Estação não encontrada para a id informada", content = @Content)
        })
        @GetMapping("/all/params_station/{id_station}")
        public ResponseEntity<List<Integer>> getAllIdParamsStation(@PathVariable Integer id_station) {
                Station s = stationService.getById(id_station);
                List<Integer> data = dataService.getAllIdParamsStation(id_station);
                return ResponseEntity.ok().body(data);
        }
}