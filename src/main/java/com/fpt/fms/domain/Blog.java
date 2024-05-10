package com.fpt.fms.domain;

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
@Getter
@Setter
@Entity
@Where(clause = "is_deleted=false")
@Table(name = "blog")
public class Blog extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Unique
    @NotNull
    @Column(name = "title")
    private String title;

    @Size( max = 5000)
    @Column(name = "content")
    private String content;

    @Size( max = 1000)
    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "image_url",length = 1000)
    private String imageUrl;


    @OneToOne
    @JoinColumn(name = "blog_category_id")
    private BlogCategory blogCategory;
}
