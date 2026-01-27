package com.tv.movie.utils;

import com.tv.movie.exception.CustomMessageException;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

@NoArgsConstructor
public class FileHandlerUtils {

    public static String extractFileExtension(String originalFileName) {
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return fileExtension;
    }

    public static Path findFileByUuid(Path dictionary, String uuid) throws IOException {
        return Files.list(dictionary)
                .filter(file -> file.getFileName().toString().startsWith(uuid))
                .findFirst()
                .orElseThrow(() -> new CustomMessageException("File not found for uuid" + uuid));
    }

    public static String detectVideoContentType(String fileName) {
        if (fileName == null) return "video/mp4";
        if (fileName.endsWith(".webm")) return "video/webm";
        if (fileName.endsWith(".ogg")) return "audio/ogg";
        if (fileName.endsWith(".mov")) return "video/quicktime";
        if (fileName.endsWith(".avi")) return "video/x-msvideo";
        if (fileName.endsWith(".flv")) return "video/x-flv";
        if (fileName.endsWith(".wmv")) return "video/x-ms-wmv";
        if (fileName.endsWith(".mkv")) return "video/x-matroska";
        if (fileName.endsWith(".m4v")) return "video/x-m4v";
        if (fileName.endsWith(".3gp")) return "video/3gpp";
        if (fileName.endsWith(".mpg") || fileName.endsWith(".mpeg")) return "video/mpeg";
        return "video/mp4";
    }

    public static String detectImageContentType(String fileName) {
        if (fileName == null) return "image/jpeg";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".gif")) return "image/gif";
        if (fileName.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }

    public static long[] parseRangeHeader(String rangerHeader, long fileLength) {
        String[] ranges = rangerHeader.replace("bytes=", "").split("-");
        long start = Long.parseLong(ranges[0]);
        long end = ranges.length > 1 && !ranges[1].isEmpty() ? Long.parseLong(ranges[1]) : fileLength - 1;
        return new long[]{start, end};
    }

    public static Resource createRangeResource(Path filePath, long rangeStart, long rangeLength) throws IOException {
        RandomAccessFile fileReader = new RandomAccessFile(filePath.toFile(), "r");

        InputStream partialContentStream = new InputStream() {

            private long totalBytesRead = 0;

            @Override
            public int read() throws IOException {
                if (totalBytesRead >= rangeLength) {
                    fileReader.close();
                    return -1;
                }
                totalBytesRead++;
                return fileReader.read();
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                if (totalBytesRead >= rangeLength) {
                    fileReader.close();
                    return -1;
                }
                long remainingBytes = rangeLength - totalBytesRead;
                int bytesToRead = (int) Math.min(len, remainingBytes);
                int bytesActualRead = fileReader.read(b, off, bytesToRead);

                if (bytesActualRead > 0) {
                    totalBytesRead += bytesActualRead;
                }
                if (totalBytesRead >= rangeLength) {
                    fileReader.close();
                }
                return bytesActualRead;
            }

            @Override
            public void close() throws IOException {
                fileReader.close();
            }
        };
        return new InputStreamResource(partialContentStream) {
            @Override
            public long contentLength() {
                return rangeLength;
            }
        };
    }

    public static Resource createFullResource(Path filePath) throws IOException {
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()){
            throw new IOException("File not found or not readable" + filePath);
        }
        return resource;
    }
}
