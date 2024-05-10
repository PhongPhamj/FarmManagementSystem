package com.fpt.fms.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Where(clause = "is_deleted=false")
@Table(name = "task")
public class Task extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    private User user;

    @Column(name = "status_process")
    @Enumerated(EnumType.STRING)
    private StatusProcess statusProcess;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "repeat_status")
    @Enumerated(EnumType.STRING)
    private RepeatStatus repeatStatus;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "repeat")
    private Long repeat;

    @Column(name = "repeat_until")
    private Date repeatUntil;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "task_tool", joinColumns = @JoinColumn(name = "task_id"), inverseJoinColumns = @JoinColumn(name = "tool_id"))
    List<Tool> toolList = new ArrayList<>();

    @OneToOne
    private Plant plant;
}
