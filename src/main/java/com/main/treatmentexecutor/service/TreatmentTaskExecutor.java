package com.main.treatmentexecutor.service;

import com.main.treatmentexecutor.model.TreatmentPlan;
import com.main.treatmentexecutor.model.TreatmentTask;
import com.main.treatmentexecutor.model.enums.TaskStatus;
import com.main.treatmentexecutor.repository.TreatmentPlanRepository;
import com.main.treatmentexecutor.repository.TreatmentTaskRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class TreatmentTaskExecutor {
    private final TreatmentPlanRepository planRepository;

    private final TreatmentTaskRepository taskRepository;

    private final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

    public TreatmentTaskExecutor(TreatmentPlanRepository planRepository, TreatmentTaskRepository taskRepository) {
        this.planRepository = planRepository;
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void init() {
        taskScheduler.setPoolSize(5);
        taskScheduler.initialize();
        startScheduler();
    }

    public void startScheduler() {
        taskScheduler.scheduleAtFixedRate(this::generateTasks, 10000);
    }

    private void generateTasks() {
        List<TreatmentPlan> plans = planRepository.findByEndTimeAfterOrEndTimeIsNull(System.currentTimeMillis());

        for (TreatmentPlan plan : plans) {
            LocalDateTime nextExecutionTime = calculateNextExecutionTime(plan);
            if (nextExecutionTime != null) {
                createTreatmentTask(plan);
            }
        }
    }

    private LocalDateTime calculateNextExecutionTime(TreatmentPlan plan) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(plan.getStartTime()), ZoneId.systemDefault());

        if ("DAILY".equals(plan.getRecurrencePattern())) {
            while (startTime.isBefore(now)) {
                startTime = startTime.plusDays(1);
            }
            return startTime;
        } else if ("WEEKLY".equals(plan.getRecurrencePattern())) {
            while (startTime.isBefore(now)) {
                startTime = startTime.plusWeeks(1);
            }
            return startTime;
        }
        return null;
    }

    private void createTreatmentTask(TreatmentPlan plan) {
        TreatmentTask task = new TreatmentTask();
        task.setSubjectPatient(plan.getSubjectPatient());
        task.setTreatmentAction(plan.getTreatmentAction());
        task.setTaskStatus(TaskStatus.ACTIVE);
        taskRepository.save(task);
    }
}