package com.example.printer_springbe.product.service;

import com.example.printer_springbe.common.exception.BusinessException;
import com.example.printer_springbe.common.response.ResponseCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductImageStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final Path uploadRoot;

    public ProductImageStorageService(
            @Value("${app.product.upload-dir:uploads/products}") String uploadDir) {
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException(
                    ResponseCode.BAD_REQUEST,
                    HttpStatus.BAD_REQUEST,
                    "Image must be JPEG, PNG, WEBP, or GIF"
            );
        }

        String extension = extensionFromContentType(contentType);
        String filename = UUID.randomUUID() + extension;

        try {
            Files.createDirectories(uploadRoot);
            Path target = uploadRoot.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException ex) {
            throw new BusinessException(
                    ResponseCode.INTERNAL_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to store product image"
            );
        }
    }

    public String toPublicUrl(String storedFilename) {
        if (!StringUtils.hasText(storedFilename)) {
            return null;
        }
        return "/uploads/products/" + storedFilename;
    }

    private static String extensionFromContentType(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
    }
}
