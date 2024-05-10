package com.fpt.fms.domain;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Where(clause = "is_deleted=false")
@Entity(name = "location_detail")
public class LocationDetail extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name_area")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "number_of_beds")
    private Integer numberOfBeds;

    @Column(name = "plant_format")
    @Enumerated(EnumType.STRING)
    private PlantFormat plantFormat;

    @Column(name = "description")
    private String description;

    @Column(name = "area")
    private Integer area;

    @OneToMany(mappedBy = "locationDetail")
    List<CropPlan> cropPlans = new ArrayList<>();

    private Boolean status;
}
