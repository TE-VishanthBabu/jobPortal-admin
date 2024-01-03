package com.job_portal.admin_portal.repository;

import com.job_portal.admin_portal.entity.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter,String> {

    Long countByVerifiedBadge(String VERIFIED);
    Long countByCreationDateBetween(Date currentDate,Date previousDate);
}
