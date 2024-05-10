package com.fpt.fms.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Where(clause = "is_deleted=false")
@Table(name = "crop_plan")
public class CropPlan extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "status")
    private Boolean status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plant_id")
    private Plant plant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_detail_id")
    private LocationDetail locationDetail;

    @Column(name = "start_method")
    @Enumerated(EnumType.STRING)
    private StartMethod startMethod;

    @Column(name = "growth_stage")
    @Enumerated(EnumType.STRING)
    private GrowthStage growthStage;

    @Column(name = "expected_amount")
    private Integer expectedAmount;

    @Column(name = "is_done")
    private Boolean isDone;

    @Column(name = "date_done")
    private Date dateDone;

    @Column(name = "date_to_harvest")
    private Date dateToHarvest;

    @Column(name = "bed")
    private String bed;

    @Column(name = "sow_amount")
    private Integer sowAmount;

    @Column(name = "germination_date")
    private Date germinationDate;

    @Column(name = "sow_date")
    private Date sowDate;

    @Column(name = "seedling_date")
    private Date seedlingDate;

    @Column(name = "flower_date")
    private Date flowerDate;

    @Column(name = "ripening_date")
    private Date ripeningDate;

    @Column(name = "complete_date")
    private Date completeDate;

    @Column(name = "seed_started")
    private Date seedStarted;

    @Column(name = "germination_amount")
    private Integer germinationAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "harvest_unit")
    private HarvestUnit harvestUnit;

    @Column(name = "session")
    @Enumerated(EnumType.STRING)
    private Session session;

    @Column(name = "from_date")
    private Date fromDate;

    @Column(name = "to_date")
    private Date toDate;

    @Column(name = "seedling_amount")
    private Integer seedlingAmount;

    @Column(name = "flower_amount")
    private Integer flowerAmount;

    @Column(name = "ripening_amount")
    private Integer ripeningAmount;

    @Column(name = "complete_amount")
    private Integer completeAmount;

    @Column(name = "plant_space")
    private Integer plantSpace;

    @Column(name = "row_space")
    private Integer rowSpace;

    @Column(name = "plant_depth")
    private Integer plantDepth;

    @OneToMany(mappedBy = "cropPlan", fetch = FetchType.EAGER)
    private List<HarvestPlan> harvestPlans;
}
