package com.fpt.fms.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.common.aliasing.qual.Unique;
import org.hibernate.annotations.Where;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Where(clause = "is_deleted=false")
@Table(name = "tool")
public class Tool extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Unique
    @NotNull
    @Column(columnDefinition = "character varying(255)")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private Boolean status;

    @OneToOne
    @JoinColumn(name = "tool_category_id")
    private ToolCategory toolCategory;

    @Column(name = "source")
    private String source;

    @Column(name = "size")
    private Integer size;
}
