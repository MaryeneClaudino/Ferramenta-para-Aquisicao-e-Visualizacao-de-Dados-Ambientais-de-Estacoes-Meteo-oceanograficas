package br.edu.ifrs.tcc.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifrs.tcc.backend.model.Station;
import br.edu.ifrs.tcc.backend.model.dto.StationDTO;
import br.edu.ifrs.tcc.backend.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Estações De Monitoramento", description = "APIs de acesso a informações das estações")
@RestController
@RequestMapping("/station")
public class StationController {

    @Autowired
    protected StationService stationService;

    @Operation(summary = "Lista informações de todas as estações", description = "Retorna uma lista de JSON que contém as informações de cada estações")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de Estações com suas informações", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Station>> getAll() {
        List<Station> stations = stationService.getAll();
        return ResponseEntity.ok().body(stations);
    }

    @Operation(summary = "Informações da estação correspondente ao id informado", description = "Retorna um JSON que contém as informações da estação referente ao id informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações da estação", content = @Content),
            @ApiResponse(responseCode = "404", description = "Estação não encontrada com o id informado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Station> getById(@PathVariable Integer id) {
        Station station = stationService.getById(id);
        return ResponseEntity.ok().body(station);
    }

    @Operation(summary = "Informações da estação correspondente ao nome informado", description = "Retorna um JSON que contém as informações da estação referente ao nome informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações da estação", content = @Content),
            @ApiResponse(responseCode = "404", description = "Estação não encontrada com o nome informado", content = @Content)
    })
    @GetMapping("/byName/{name}")
    public ResponseEntity<Station> getByName(@PathVariable String name) {
        Station station = stationService.getByName(name);
        return ResponseEntity.ok().body(station);
    }

    @Operation(summary = "Insere uma nova estação", description = "Retorna um JSON que contém as informações da estação inserida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações da estação inseridos com sucesso", content = @Content),
            @ApiResponse(responseCode = "409", description = "Erro ao inserir, informações da estação duplicadas ", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro nos dados da estação", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Station> save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "O acquirer_importer_cfg tem duas configurações, onde o campo regularity é a frequência dos dados nos arquivos e o campo file_out_types é o nome dos tipos dos arquivos que se deseja como saída", required = true, content = @Content(schema = @Schema(implementation = StationDTO.class, example = "{\n"
                    + //
                    "  \"name\": \"string\",\n" + //
                    "  \"latitude\": 10.1,\n" + //
                    "  \"longitude\": 15.5,\n" + //
                    "  \"status\": \"string\",\n" + //
                    "  \"installation\": \"1970-01-01T00:00:00.0Z\",\n" + //
                    "  \"project\": \"string\",\n" + //
                    "  \"path_data_files\": \"string\",\n" + //
                    "  \"acquirer_importer_cfg_str\": \"{\\\"regularity\\\": \\\"hr\\\", \\\"file_out_types\\\": [\\\"perfil_dens\\\", \\\"perfil_temp\\\", \\\"perfil_sal\\\", \\\"met\\\", \\\"doppler\\\"]}\"\n"
                    + //
                    "}"))) @RequestBody StationDTO stationDto) {
        Station station = stationService.save(stationDto);
        return ResponseEntity.ok().body(station);
    }

    @Operation(summary = "Atualiza uma estação", description = "Retorna um JSON que contém as informações da estação editada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações da estação atualizados com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Estação não encontrada com o id informado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Erro ao atualizar o nome da estação, deve ser um nome único", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro nos dados da estação", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Station> update(@PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "O acquirer_importer_cfg tem duas configurações, onde o campo regularity é a frequência dos dados nos arquivos e o campo file_out_types é o nome dos tipos dos arquivos que se deseja como saída", required = true, content = @Content(schema = @Schema(implementation = StationDTO.class, example = "{\n"
                    + //
                    "  \"name\": \"string\",\n" + //
                    "  \"latitude\": 10.1,\n" + //
                    "  \"longitude\": 15.5,\n" + //
                    "  \"status\": \"string\",\n" + //
                    "  \"installation\": \"1970-01-01T00:00:00.0Z\",\n" + //
                    "  \"project\": \"string\",\n" + //
                    "  \"path_data_files\": \"string\",\n" + //
                    "  \"acquirer_importer_cfg_str\": \"{\\\"regularity\\\": \\\"hr\\\", \\\"file_out_types\\\": [\\\"met\\\", \\\"doppler\\\"]}\"\n"
                    + //
                    "}"))) @RequestBody StationDTO stationDto) {
        Station station = stationService.update(id, stationDto);
        return ResponseEntity.ok().body(station);
    }

    @Operation(summary = "Deleta uma estação", description = "Retorna 'true' se a estação foi deletada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estação deletada com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Estação não encontrada com o id informado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Estação não pode ser deletada, existem dados vinculados.", content = @Content),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Integer id) {
        boolean deleted = stationService.delete(id);
        if (deleted) {
            return new ResponseEntity<Boolean>(deleted, HttpStatus.OK);
        } else {
            return new ResponseEntity<Boolean>(deleted, HttpStatus.NOT_FOUND);
        }
    }
}