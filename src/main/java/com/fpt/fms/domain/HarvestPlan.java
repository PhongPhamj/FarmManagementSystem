package com.fpt.fms.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Where(clause = "is_deleted=false")
@Table(name = "harvest_plan")
public class HarvestPlan extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "date_harvest")
    private Date dateHarvest;

    @Column(name = "quality", length = 290)
    private String quality;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "crop_plan_id")
    private CropPlan cropPlan;

}
