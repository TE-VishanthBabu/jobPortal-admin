package com.job_portal.admin_portal.repository;

import com.job_portal.admin_portal.entity.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyProfileRepository extends JpaRepository<CompanyProfile,String> {
    CompanyProfile findByRecruiterId(String recruiterId);
}
