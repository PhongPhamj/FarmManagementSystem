package com.fpt.fms.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.common.aliasing.qual.Unique;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Where(clause = "is_deleted=false")
@Table(name = "rank")
public class Rank extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Unique
    @NotNull
    @Column(columnDefinition = "character varying(255)")
    private String name;

    @NotNull
    @Column(name = "description")
    private String description;

    @NotNull
    @Size(max = 5000)
    @Column(name = "rank_detail")
    private String rankDetail;

    @NotNull
    @Column(name = "status")
    private Boolean status;
}
