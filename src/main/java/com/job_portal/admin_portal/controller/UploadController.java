package com.job_portal.admin_portal.controller;

import com.job_portal.admin_portal.response.JobPortalResponse;
import com.job_portal.admin_portal.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class UploadController {
    private final UploadService uploadService;
    private final MessageSource messageSource;

    /**
     * Image upload.
     *
     * @param file  Multipart file
     * @return uploaded image response
     */
    @PostMapping("/upload/image")
    public ResponseEntity<JobPortalResponse> uploadImage(@RequestPart MultipartFile file) {
        String fileName = this.uploadService.uploadImage(file);
        log.info("Uploaded image filename: {}",fileName);
        JobPortalResponse response = new JobPortalResponse();
        response.setData(fileName);
        response.setMessage(messageSource.getMessage("image.upload",null, Locale.getDefault()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Pdf upload.
     *
     * @param file  Multipart file
     * @return uploaded pdf response
     */
    @PostMapping("/upload/pdf")
    public ResponseEntity<JobPortalResponse> uploadPdf(@RequestPart MultipartFile file) {
        String fileName = this.uploadService.uploadPdf(file);
        log.info("Uploaded pdf filename: {}",fileName);
        JobPortalResponse response = new JobPortalResponse();
        response.setData(fileName);
        response.setMessage(messageSource.getMessage("pdf.upload",null,Locale.getDefault()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
