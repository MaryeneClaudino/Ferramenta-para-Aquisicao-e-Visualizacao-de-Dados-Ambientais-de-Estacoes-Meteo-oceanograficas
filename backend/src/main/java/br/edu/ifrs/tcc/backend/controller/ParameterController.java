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

import br.edu.ifrs.tcc.backend.model.Parameter;
import br.edu.ifrs.tcc.backend.model.dto.ParameterDTO;
import br.edu.ifrs.tcc.backend.service.ParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Parâmetros", description = "APIs de acesso a informações dos parâmetros das estações")
@RestController
@RequestMapping("/parameter")
public class ParameterController {

    @Autowired
    protected ParameterService parameterService;

    @Operation(summary = "Lista informações de todos os parâmetros", description = "Retorna uma lista de JSON que contém as informações de cada parâmetro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de parâmetros com suas informações", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Parameter>> getAll() {
        List<Parameter> parameter = parameterService.getAll();
        return ResponseEntity.ok().body(parameter);
    }

    @Operation(summary = "Informações do parâmetro correspondente ao id informado", description = "Retorna um JSON que contém as informações do parâmetro referente ao id informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações do parâmetro", content = @Content),
            @ApiResponse(responseCode = "404", description = "Parâmetro não encontrado com o id informado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Parameter> getById(@PathVariable Integer id) {
        Parameter parameter = parameterService.getById(id);
        return ResponseEntity.ok().body(parameter);
    }

    @Operation(summary = "Informações do parâmetro correspondente ao nome informado", description = "Retorna um JSON que contém as informações do parâmetro referente ao nome informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações do parâmetro", content = @Content),
            @ApiResponse(responseCode = "404", description = "Parâmetro não encontrado com o nome informado", content = @Content)
    })
    @GetMapping("/byName/{name}")
    public ResponseEntity<Parameter> getByName(@PathVariable String name) {
        Parameter parameter = parameterService.getByName(name);
        return ResponseEntity.ok().body(parameter);
    }

    @Operation(summary = "Insere um novo parâmetro", description = "Retorna um JSON que contém as informações do parâmetro inserido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações do parâmetro inseridos com sucesso", content = @Content),
            @ApiResponse(responseCode = "409", description = "Erro ao inserir, informações do parâmetro duplicadas ", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro nos dados do parâmetro", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Parameter> save(@RequestBody ParameterDTO parameterDto) {
        Parameter parameter = parameterService.save(parameterDto);
        return ResponseEntity.ok().body(parameter);
    }

    @Operation(summary = "Atualiza um parâmetro", description = "Retorna um JSON que contém as informações do parâmetro editado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações do parâmetro atualizados com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Parâmetro não encontrado com o id informado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Erro ao atualizar o nome do parâmetro, deve ser um nome único", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro nos dados do parâmetro", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Parameter> update(@PathVariable Integer id, @RequestBody ParameterDTO parameterDto) {
        Parameter parameter = parameterService.update(id, parameterDto);
        return ResponseEntity.ok().body(parameter);
    }

    @Operation(summary = "Deleta um parâmetro", description = "Retorna 'true' se o parâmetro foi deletado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parâmetro deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Parâmetro não encontrado com o id informado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Parâmetro não pode ser deletado, existem dados vinculados.", content = @Content),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Integer id) {
        boolean deleted = parameterService.delete(id);
        if (deleted) {
            return new ResponseEntity<Boolean>(deleted, HttpStatus.OK);
        } else {
            return new ResponseEntity<Boolean>(deleted, HttpStatus.NOT_FOUND);
        }
    }
}