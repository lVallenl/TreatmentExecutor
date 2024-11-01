package com.main.treatmentexecutor.service;

import com.main.treatmentexecutor.model.TreatmentPlan;
import com.main.treatmentexecutor.model.TreatmentTask;
import com.main.treatmentexecutor.model.enums.TaskStatus;
import com.main.treatmentexecutor.repository.TreatmentPlanRepository;
import com.main.treatmentexecutor.repository.TreatmentTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TreatmentTaskExecutor {
    @Autowired
    private TreatmentPlanRepository planRepository;

    @Autowired
    private TreatmentTaskRepository taskRepository;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final long CHECK_INTERVAL_SECONDS = 10; // Check every 10 seconds

    @PostConstruct
    public void startScheduler() {
        scheduleNextRun();
    }

    @PreDestroy
    public void stopScheduler() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            scheduler.shutdownNow();
        }
    }

    private void scheduleNextRun() {
        CompletableFuture.runAsync(this::generateTasks, scheduler)
                .thenRunAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(CHECK_INTERVAL_SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    scheduleNextRun(); // Schedule the next run recursively
                });
    }

    private void generateTasks() {
        long currentTime = Instant.now().toEpochMilli();
        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTime), ZoneId.systemDefault());
        List<TreatmentPlan> plans = planRepository.findByEndTimeAfterOrEndTimeIsNull(currentTime);

        for (TreatmentPlan plan : plans) {
            LocalDateTime nextExecutionTime = calculateNextExecutionTime(plan, now);
            if (nextExecutionTime != null && nextExecutionTime.isBefore(now.plusMinutes(10))) {
                createTreatmentTask(plan, nextExecutionTime);
            }
        }
    }

    private LocalDateTime calculateNextExecutionTime(TreatmentPlan plan, LocalDateTime referenceTime) {
        LocalDateTime startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(plan.getStartTime()), ZoneId.systemDefault());

        if (plan.getRecurrencePattern().equals("DAILY")) {
            while (startTime.isBefore(referenceTime)) {
                startTime = startTime.plusDays(1);
            }
            return startTime;
        }
        if (plan.getRecurrencePattern().equals("WEEKLY")) {
            while (startTime.isBefore(referenceTime)) {
                startTime = startTime.plusWeeks(1);
            }
            return startTime;
        }
        return null;
    }

    private void createTreatmentTask(TreatmentPlan plan, LocalDateTime executionTime) {
        TreatmentTask task = new TreatmentTask();
        task.setSubjectPatient(plan.getSubjectPatient());
        task.setTreatmentAction(plan.getTreatmentAction());
        task.setExecutionTime(executionTime);
        task.setTaskStatus(TaskStatus.ACTIVE);

        taskRepository.save(task);
    }
}