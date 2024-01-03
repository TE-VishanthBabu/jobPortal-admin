package com.job_portal.admin_portal.repository;

import com.job_portal.admin_portal.entity.MeetingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingScheduleRepository extends JpaRepository<MeetingSchedule,String> {
}