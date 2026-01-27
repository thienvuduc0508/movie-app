package com.tv.movie.serviceImpl;

import com.tv.movie.exception.CustomMessageException;
import com.tv.movie.service.FileUploadService;
import com.tv.movie.utils.FileHandlerUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private Path videoStorageLocation;
    private Path imageStorageLocation;

    @Value("${file.upload.video-dir}")
    private String videoDir;

    @Value("${file.upload.image-dir}")
    private String imageDir;

    @PostConstruct
    public void init() {
        videoStorageLocation = Paths.get(videoDir).toAbsolutePath().normalize();
        imageStorageLocation = Paths.get(imageDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(videoStorageLocation);
            Files.createDirectories(imageStorageLocation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String storeVideoFile(MultipartFile file) {
        return storeFile(file, videoStorageLocation);
    }

    @Override
    public String storeImageFile(MultipartFile file) {
        return storeFile(file, imageStorageLocation);
    }

    @Override
    public ResponseEntity<Resource> serveVideo(String uuid, String rangeHeader) {
        try {
            Path filePath = FileHandlerUtils.findFileByUuid(videoStorageLocation, uuid);
            Resource resource = FileHandlerUtils.createFullResource(filePath);
            String fileName = resource.getFilename();
            String contentType = FileHandlerUtils.detectVideoContentType(fileName);
            long fileLength = resource.contentLength();

            if(isFullContentRequest(rangeHeader)) {
                return buildFullContentResponse(resource, contentType, fileName, fileLength);
            }
            return buildPartialContentResponse(filePath, rangeHeader, contentType, fileName, fileLength);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<Resource> serveImage(String uuid) {
        try {
            Path filePath = FileHandlerUtils.findFileByUuid(imageStorageLocation, uuid);
            Resource resource = FileHandlerUtils.createFullResource(filePath);
            String fileName = resource.getFilename();
            String contentType = FileHandlerUtils.detectImageContentType(fileName);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<Resource> buildPartialContentResponse(Path filePath, String rangeHeader, String contentType, String fileName, long fileLength) throws IOException {
        long[] range = FileHandlerUtils.parseRangeHeader(rangeHeader, fileLength);
        long rangeStart = range[0];
        long rangeEnd = range[1];
        if (!isValidRange(rangeStart, rangeEnd, fileLength)){
            return buildRangeNotSatisfiableResponse(fileLength);
        }
        long contentLength = rangeEnd - rangeStart + 1;
        Resource rangeResource = FileHandlerUtils.createRangeResource(filePath, rangeStart, contentLength);

        return ResponseEntity.status(206)
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength))
                .body(rangeResource);
        }

    private ResponseEntity<Resource> buildRangeNotSatisfiableResponse(long fileLength) {
        return ResponseEntity.status(416)
                .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength).build();

    }

    private boolean isValidRange(long rangeStart, long rangeEnd, long fileLength) {
        return rangeStart <= rangeEnd && rangeStart >= 0 && rangeEnd < fileLength;
    }

    private ResponseEntity<Resource> buildFullContentResponse(Resource resource, String contentType, String fileName, long fileLength) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength))
                .body(resource);
    }

    private boolean isFullContentRequest(String rangeHeader) {
        return rangeHeader == null || rangeHeader.isEmpty();
    }

    private String storeFile(MultipartFile file, Path storageLocation) {
        String fileExtension = FileHandlerUtils.extractFileExtension(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        String filename = uuid + fileExtension;

        if(file.isEmpty()) {
            throw new CustomMessageException("File is empty");
        }
        try {
            Path targetLocation = storageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return uuid;
        } catch (IOException ex) {
            throw new CustomMessageException("Could not store file " + filename + ". Please try again!");
        }
    }
}
