package com.job_portal.admin_portal.repository;

import com.job_portal.admin_portal.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication,String> {
    List<JobApplication> findAllByJobPostId(String jobPostId);
    List<JobApplication> findAllByJobSeekerId(String jobSeekerId);

    Long countByJobPostId(String jobPostId);
}
