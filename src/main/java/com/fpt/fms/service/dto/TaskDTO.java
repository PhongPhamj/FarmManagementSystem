package com.fpt.fms.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fpt.fms.domain.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDTO {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @JsonProperty("status")
    private Boolean status;

    @JsonProperty("assigned_user_id")
    private Long user;

    @JsonProperty("assigned_user_name")
    private String userName;

    @JsonProperty("status_process")
    @Enumerated(EnumType.STRING)
    private StatusProcess statusProcess;

    @JsonProperty("priority")
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonProperty("due_date")
    private Date dueDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonProperty("start_date")
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @JsonProperty("start_time")
    private Date startTimme;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @JsonProperty("due_time")
    private Date dueTimme;

    @JsonProperty("repeat_status")
    @Enumerated(EnumType.STRING)
    private RepeatStatus repeatStatus;

    @JsonProperty("title")
    private String title;

    @JsonProperty("repeat")
    private Long repeat;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonProperty("repeat_until")
    private Date repeatUntil;

    @JsonProperty("description")
    private String description;

    private List<Long> toolIds;

    @JsonProperty("createdBy")
    private String createBy;
    private Long plantId;

    private String plantName;
    private String plantSource;

    private Long startHourse;
    private Long startMinutes;
    private Long endHourse;
    private Long endMinutes;

    public Date getStartTime() {
        if (startHourse != null && startMinutes != null) {
            // Khởi tạo Calendar với ngày và giờ từ startDate
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            // Đặt giờ và phút từ startHourse và startMinutes
            calendar.set(Calendar.HOUR_OF_DAY, startHourse.intValue());
            calendar.set(Calendar.MINUTE, startMinutes.intValue());

            return calendar.getTime();
        }
        return null;
    }

    public Date getDueTime() {
        if (endHourse != null && endMinutes != null) {
            // Khởi tạo Calendar với ngày và giờ từ dueDate
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dueDate);

            // Đặt giờ và phút từ endHourse và endMinutes
            calendar.set(Calendar.HOUR_OF_DAY, endHourse.intValue());
            calendar.set(Calendar.MINUTE, endMinutes.intValue());

            return calendar.getTime();
        }
        return null;
    }
}
