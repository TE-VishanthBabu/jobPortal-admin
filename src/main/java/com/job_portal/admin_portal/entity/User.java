package com.job_portal.admin_portal.entity;

import com.job_portal.admin_portal.entity.generic.AbstractEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User extends AbstractEntity {
    private String userName;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String profileImage;
}
