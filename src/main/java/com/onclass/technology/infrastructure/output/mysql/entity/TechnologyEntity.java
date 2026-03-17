package com.onclass.technology.infrastructure.output.mysql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Representa la entidad de infraestructura persistida en la tabla de tecnologias.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("technologies")
public class TechnologyEntity {

    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("normalized_name")
    private String normalizedName;

    @Column("description")
    private String description;

}

