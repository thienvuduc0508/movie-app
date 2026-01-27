package com.tv.movie.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.attribute.AclEntry;

public interface FileUploadService {
    String storeVideoFile(MultipartFile file);

    String storeImageFile(MultipartFile file);

    ResponseEntity<Resource> serveVideo(String uuid, String rangeHeader);

    ResponseEntity<Resource> serveImage(String uuid);
}
