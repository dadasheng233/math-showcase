package com.mathshowcase.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.storage.mode}")
    private String storageMode;

    @Value("${file.storage.local.upload-dir}")
    private String localUploadDir;

    @Value("${file.storage.oss.endpoint}")
    private String ossEndpoint;

    @Value("${file.storage.oss.access-key-id}")
    private String accessKeyId;

    @Value("${file.storage.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${file.storage.oss.bucket-name}")
    private String bucketName;

    public String store(MultipartFile file, String subDir) throws IOException {
        if ("oss".equalsIgnoreCase(storageMode)) {
            return storeToOss(file, subDir);
        }
        return storeToLocal(file, subDir);
    }

    private String storeToLocal(MultipartFile file, String subDir) throws IOException {
        Path uploadPath = Paths.get(localUploadDir, subDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String storedName = UUID.randomUUID().toString() + ext;
        Path targetPath = uploadPath.resolve(storedName);
        file.transferTo(targetPath.toFile());
        return subDir + "/" + storedName;
    }

    private String storeToOss(MultipartFile file, String subDir) throws IOException {
        OSS ossClient = new OSSClientBuilder().build(ossEndpoint, accessKeyId, accessKeySecret);
        try {
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String objectName = subDir + "/" + UUID.randomUUID().toString() + ext;
            ossClient.putObject(bucketName, objectName, file.getInputStream());
            return objectName;
        } finally {
            ossClient.shutdown();
        }
    }

    public String getStorageMode() {
        return storageMode;
    }
}
