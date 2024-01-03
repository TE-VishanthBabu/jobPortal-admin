package com.job_portal.admin_portal.customException;

public class InvalidFileFormat extends RuntimeException{
    public InvalidFileFormat(String message){
        super(message);
    }
}
