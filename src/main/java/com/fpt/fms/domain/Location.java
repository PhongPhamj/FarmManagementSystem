package com.fpt.fms.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Where(clause = "is_deleted=false")
@Entity(name = "location")
public class Location extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "address")
    private String address;


    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude ;

    private Boolean status;

    @OneToMany(mappedBy = "location")
    List<LocationDetail> locationDetails = new ArrayList<>();

}
