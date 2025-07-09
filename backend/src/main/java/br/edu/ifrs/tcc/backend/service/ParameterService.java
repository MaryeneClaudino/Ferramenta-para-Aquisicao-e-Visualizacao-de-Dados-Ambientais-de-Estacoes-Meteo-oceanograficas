package br.edu.ifrs.tcc.backend.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifrs.tcc.backend.model.Parameter;
import br.edu.ifrs.tcc.backend.model.dto.ParameterDTO;
import br.edu.ifrs.tcc.backend.repository.ParameterRepository;
import br.edu.ifrs.tcc.backend.service.exceptions.NotFoundException;

@Service
public class ParameterService {

    @Autowired
    private ParameterRepository parameterRepository;

    public Long count() {
        return parameterRepository.count();
    }

    public List<Parameter> getAll() {
        return parameterRepository.findAll();
    }

    public Parameter getById(int id) {
        return parameterRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Parâmetro (" + id + ") não encontrado."));
    }

    public Parameter getByName(String name) {
        return parameterRepository.findByName(name.toUpperCase())
                .orElseThrow(() -> new NotFoundException("Parâmetro (" + name + ") não encontrado."));
    }

    public Parameter save(ParameterDTO parameterDto) {
        Parameter parameter = new Parameter();
        parameter.setName(parameterDto.name());
        parameter.setDescription(parameterDto.description());
        parameter.setType_class(parameterDto.type_class());
        parameter.setUnit(parameterDto.unit());
        parameter.setEquipment(parameterDto.equipment());

        return parameterRepository.save(parameter);
    }

    public Parameter save(Parameter parameter) {
        return parameterRepository.save(parameter);
    }

    public Parameter update(int id, ParameterDTO parameterDto) {
        Parameter parameter = getById(id);

        if (parameterDto.name() != null && !parameterDto.name().trim().isEmpty()) {
            parameter.setName(parameterDto.name());
        }
        if (parameterDto.description() != null && !parameterDto.description().trim().isEmpty()) {
            parameter.setDescription(parameterDto.description());
        }
        if (parameterDto.type_class() != null && !parameterDto.type_class().trim().isEmpty()) {
            parameter.setType_class(parameterDto.type_class());
        }
        if (parameterDto.unit() != null && !parameterDto.unit().trim().isEmpty()) {
            parameter.setUnit(parameterDto.unit());
        }
        if (parameterDto.equipment() != null && !parameterDto.equipment().trim().isEmpty()) {
            parameter.setEquipment(parameterDto.equipment());
        }

        return parameterRepository.save(parameter);
    }

    public boolean delete(int id) {
        if (parameterRepository.existsById(id)) {
            parameterRepository.deleteById(id);
            return true;
        } else {
            throw new NotFoundException("Parâmetro (" + id + ") não encontrado.");
        }
    }
}