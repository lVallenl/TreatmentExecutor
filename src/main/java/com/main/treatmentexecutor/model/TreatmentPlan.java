package com.main.treatmentexecutor.model;

import com.main.treatmentexecutor.model.enums.TreatmentAction;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "treatment_plan")
public class TreatmentPlan {

    @Id
    private Long id;

    private TreatmentAction treatmentAction;

    private String subjectPatient;

    //StartTime of TreatmentPlan == startTime of first possible TreatmentAction (strange description)
    private Long startTime;

    private Long endTime;

    //Added field, according to existing CodingTask description
    //Duration of one TreatmentAction from current TreatmentPlan
    private Long taskDuration;

    private String recurrencePattern;


}
