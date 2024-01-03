package com.job_portal.admin_portal.repository;

import com.job_portal.admin_portal.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost,String> {
    JobPost findByIdAndPostStatusTrueAndDeletedFalse(String jobPostId);
}
