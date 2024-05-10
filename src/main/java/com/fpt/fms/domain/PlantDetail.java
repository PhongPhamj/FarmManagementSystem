package com.fpt.fms.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Where(clause = "is_deleted=false")
@Table(name = "plant_detail")
public class PlantDetail extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @OneToOne
    @JoinColumn(name = "plant_id")
    private Plant plant;

    @Column(name = "start_method")
    @Enumerated(EnumType.STRING)
    private StartMethod startMethod;

    @Column(name = "rate_germination")
    private Integer rateGermination;

    @Column(name = "loss_rate")
    private Integer lossRate;

    @Column(name = "day_to_emerge")
    private Integer dayToEmerge;

    @Column(name = "day_to_mature")
    private Integer dayToMature;

    @Column(name = "day_to_harvest")
    private Integer dayToHarvest;

    @Column(name = "plant_space")
    private Integer plantSpace;

    @Column(name = "row_space")
    private Integer rowSpace;

    @Column(name = "plant_depth")
    private Integer plantDepth;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "harvest_unit")
    private HarvestUnit harvestUnit;

}
