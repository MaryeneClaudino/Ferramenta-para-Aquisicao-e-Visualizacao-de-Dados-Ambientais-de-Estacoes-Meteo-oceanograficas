package br.edu.ifrs.tcc.backend.model;

import java.io.Serializable;
import java.sql.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@IdClass(DataKey.class)
@Table(name = "data")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Data implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "id_station", referencedColumnName = "id")
    private Station station;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_parameter", referencedColumnName = "id")
    private Parameter parameter;

    @Id
    @Column(nullable = false)
    private Timestamp timestamp;

    @Column(nullable = false)
    private Double value;
}