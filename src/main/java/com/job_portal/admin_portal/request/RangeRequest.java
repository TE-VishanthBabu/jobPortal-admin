package com.job_portal.admin_portal.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RangeRequest {
    private Integer min;
    private Integer max;
}
