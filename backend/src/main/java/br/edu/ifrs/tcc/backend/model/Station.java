package br.edu.ifrs.tcc.backend.model;

import java.io.Serializable;
import java.sql.Timestamp;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "station")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Station implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 48, nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(length = 24, nullable = false, columnDefinition = "varchar(24) default 'undefined'")
    private String status = "undefined";;

    @Column(nullable = false)
    private Timestamp installation;

    @Column(length = 24, nullable = false, columnDefinition = "varchar(24) default 'PIRATA'")
    private String project = "PIRATA";

    @Column(length = 128, nullable = false)
    private String path_data_files;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "acquirer_importer_cfg", columnDefinition = "json")
    private String acquirer_importer_cfg_str;
}