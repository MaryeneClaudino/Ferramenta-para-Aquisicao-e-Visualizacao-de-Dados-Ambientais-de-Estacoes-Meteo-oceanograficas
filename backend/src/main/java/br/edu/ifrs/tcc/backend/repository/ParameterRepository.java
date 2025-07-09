package br.edu.ifrs.tcc.backend.repository;

import org.springframework.stereotype.Repository;
import br.edu.ifrs.tcc.backend.model.Parameter;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Integer> {

    Optional<Parameter> findByName(String name);
}