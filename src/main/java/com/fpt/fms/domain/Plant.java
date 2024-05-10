package com.fpt.fms.domain;


import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Where(clause = "is_deleted=false")
@Table(name = "plant")
public class Plant extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Size(max = 60)
    @Column(name = "name", length = 60)
    private String name;

    @Column(name = "provider")
    private String provider;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "plan_category")
    @Enumerated(EnumType.STRING)
    private PlantCategory type;

    @Column(name = "source")
    private String source;
}
