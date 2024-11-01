package com.main.treatmentexecutor.repository;

import com.main.treatmentexecutor.model.TreatmentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreatmentTaskRepository extends JpaRepository<TreatmentTask, Long> {
}
