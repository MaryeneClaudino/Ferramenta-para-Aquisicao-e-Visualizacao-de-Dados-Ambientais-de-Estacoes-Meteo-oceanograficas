package br.edu.ifrs.tcc.backend.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifrs.tcc.backend.model.Station;
import br.edu.ifrs.tcc.backend.model.dto.StationDTO;
import br.edu.ifrs.tcc.backend.repository.StationRepository;
import br.edu.ifrs.tcc.backend.service.exceptions.NotFoundException;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    public Long count() {
        return stationRepository.count();
    }

    public List<Station> getAll() {
        return stationRepository.findAll();
    }

    public Station getById(int id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Estação (" + id + ") não encontrada."));
    }

    public Station getByName(String name) {
        return stationRepository.findByName(name.toUpperCase())
                .orElseThrow(() -> new NotFoundException("Estação (" + name + ") não encontrada."));
    }

    public Station save(StationDTO stationDto) {
        Station station = new Station();
        station.setName(stationDto.name());
        station.setLatitude(stationDto.latitude());
        station.setLongitude(stationDto.longitude());
        station.setStatus(stationDto.status());
        station.setInstallation(stationDto.installation());
        station.setProject(stationDto.project());
        station.setPath_data_files(stationDto.path_data_files());
        station.setAcquirer_importer_cfg_str(stationDto.acquirer_importer_cfg_str());

        return stationRepository.save(station);
    }

    public Station save(Station station) {
        return stationRepository.save(station);
    }

    public Station update(int id, StationDTO stationDto) {
        Station station = getById(id);

        if (stationDto.name() != null && !stationDto.name().trim().isEmpty()) {
            station.setName(stationDto.name());
        }
        if (stationDto.latitude() != null) {
            station.setLatitude(stationDto.latitude());
        }
        if (stationDto.longitude() != null) {
            station.setLongitude(stationDto.longitude());
        }
        if (stationDto.status() != null && !stationDto.status().trim().isEmpty()) {
            station.setStatus(stationDto.status());
        }
        if (stationDto.installation() != null) {
            station.setInstallation(stationDto.installation());
        }
        if (stationDto.project() != null && !stationDto.project().trim().isEmpty()) {
            station.setProject(stationDto.project());
        }
        if (stationDto.path_data_files() != null && !stationDto.path_data_files().trim().isEmpty()) {
            station.setPath_data_files(stationDto.path_data_files());
        }
        if (stationDto.acquirer_importer_cfg_str() != null
                && !stationDto.acquirer_importer_cfg_str().trim().isEmpty()) {
            station.setAcquirer_importer_cfg_str(stationDto.acquirer_importer_cfg_str().toString());
        }

        return stationRepository.save(station);
    }

    public boolean delete(int id) {
        if (stationRepository.existsById(id)) {
            stationRepository.deleteById(id);
            return true;
        } else {
            throw new NotFoundException("Estação (" + id + ") não encontrada.");
        }
    }
}