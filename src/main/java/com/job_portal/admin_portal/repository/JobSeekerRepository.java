package com.job_portal.admin_portal.repository;

import com.job_portal.admin_portal.entity.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobSeekerRepository extends JpaRepository<JobSeeker,String> {
    Optional<JobSeeker> findByUserId(String userId);

    List<JobSeeker> findAllByVerifiedBadgeEquals(String badge);

    Long countByVerifiedBadge(String VERIFIED);
    Long countByCreationDateBetween(Date currentDate,Date previousDate);
}
