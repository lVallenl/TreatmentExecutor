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

    private Long startTime;

    private TaskStatus taskStatus;
}
