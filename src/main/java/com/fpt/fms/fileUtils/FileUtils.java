package com.fpt.fms.fileUtils;

import com.fpt.fms.aop.logging.LogServiceImpl;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtils {

    private static final String UPLOAD_DIR = "static/images/";
    private String bucketName = "sep490web.appspot.com";
    private static final Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);

    private final ResourceLoader resourceLoader;

    private final Storage storage;

    public FileUtils(ResourceLoader resourceLoader, Storage storage) {
        this.resourceLoader = resourceLoader;
        this.storage = storage;
    }

    public void delete(String name) throws IOException {}

    public String uploadImage(MultipartFile file, String folder, String fileNameRandom) {
        BlobId blobId = BlobId.of(bucketName, folder + "/" + fileNameRandom);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        try {
            storage.create(blobInfo, file.getInputStream().readAllBytes());
        } catch (Exception e) {
            if (!(e instanceof ClassNotFoundException)) {
                logger.error("error uploadImage: " + e.getMessage());
            }
        }
        URL url = storage.signUrl(blobInfo, 6, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature());
        String signedPath = url.toString();
        return signedPath;
    }

    public String urlDefaultImage() {
        BlobId blobId = BlobId.of(bucketName, "images/default.png");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/png").build();

        URL url = storage.signUrl(blobInfo, 6, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature());
        String signedPath = url.toString();

        return signedPath;
    }

    public String uploadImageLocal(MultipartFile file, String folder, String fileName, Long id) {
        try {
            // Xây dựng đường dẫn tới thư mục lưu trữ
            Path uploadPath = Paths.get(UPLOAD_DIR, folder, id.toString()).normalize();

            // Kiểm tra thư mục tồn tại, nếu không tồn tại, tạo nó
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Lưu tệp ảnh vào thư mục lưu trữ
            Path targetLocation = uploadPath.resolve(fileName);

            // Lưu tệp ảnh vào thư mục lưu trữ
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Tính đường dẫn tương đối từ UPLOAD_DIR
            Path relativePath = Paths.get("").relativize(Paths.get(UPLOAD_DIR).relativize(targetLocation));

            // Concatenate the constant part of the path
            String fullPath = "\\static\\images" + "\\" + relativePath.toString();

            return fullPath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
