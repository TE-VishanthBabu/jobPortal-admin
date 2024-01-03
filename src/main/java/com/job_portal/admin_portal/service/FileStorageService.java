package com.job_portal.admin_portal.service;

import com.job_portal.admin_portal.customException.CommonException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class FileStorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Store file in local.
     *
     * @param profilePicture
     * @param itemId
     * @return filename
     */
    @SneakyThrows
    public String storeFile(MultipartFile profilePicture, String itemId) {
        Path fileStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception ex) {
            log.error("Error! Error creating directory.");
            throw new CommonException("Could not create the directory where the " +
                    "uploaded files will be stored.");
        }
        String fileName = StringUtils.cleanPath(profilePicture.getOriginalFilename());
        try {
            if (fileName.contains("..")) {
                log.error("Error! Invalid File name");
                throw new CommonException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            String ext = profilePicture.getOriginalFilename()
                    .substring(profilePicture.getOriginalFilename().lastIndexOf(".") + 1);
            fileName = StringUtils.cleanPath(itemId + "." + ext);
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(profilePicture.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

             return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(fileName)
                    .toUriString();
        } catch (IOException ex) {
            log.error("Error! Uploading error. {}", ex.getMessage());
            throw new CommonException("Could not store file " + fileName + ". Please try again!");
        }
    }
}
