package br.edu.ifrs.tcc.backend.model;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "parameter")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Parameter implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 48, nullable = false, unique = true)
    private String name;

    @Column(length = 512)
    private String description;

    @Column(length = 64, nullable = false)
    private String type_class;

    @Column(length = 45, nullable = false)
    private String unit;

    @Column(length = 64)
    private String equipment;
}