package com.main.treatmentexecutor.repository;

import com.main.treatmentexecutor.model.TreatmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentPlanRepository extends JpaRepository<TreatmentPlan, Long> {
        List<TreatmentPlan> findByEndTimeAfterOrEndTimeIsNull(Long endTime);
}