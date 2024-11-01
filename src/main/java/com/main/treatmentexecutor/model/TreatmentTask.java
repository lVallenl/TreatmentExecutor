package com.main.treatmentexecutor.model;

import com.main.treatmentexecutor.model.enums.TaskStatus;
import com.main.treatmentexecutor.model.enums.TreatmentAction;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "treatment_task")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subjectPatient;

    private TreatmentAction treatmentAction;

    //"Start time: timestamp of the earliest time the task can be completed" - ? Why "startTime"?
    private Long startTime;

    private LocalDateTime executionTime;

    private TaskStatus taskStatus;
}
