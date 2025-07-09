package br.edu.ifrs.tcc.backend.repository;

import org.springframework.stereotype.Repository;
import br.edu.ifrs.tcc.backend.model.Station;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface StationRepository extends JpaRepository<Station, Integer> {

    Optional<Station> findByName(String name);
}