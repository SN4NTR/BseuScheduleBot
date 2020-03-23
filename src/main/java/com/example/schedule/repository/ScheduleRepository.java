package com.example.schedule.repository;

import com.example.schedule.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Aliaksandr Miron
 */
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
}
