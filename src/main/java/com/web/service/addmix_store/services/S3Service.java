package com.web.service.addmix_store.services;

import java.io.IOException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;
    private final String bucketName;
    // private final String cloudFrontDomain;
    

    public String uploadProductImage(Long productId, MultipartFile file, String imageType) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }
            // generate unique name 
            String fileName = generateFileName(productId, "PRODUCT", file.getOriginalFilename(), imageType);
            
            // upload on S3
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            
            s3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);

            // generated file URL on S3
            return generateImageUrl(fileName);
            
        } catch (IOException e) {
            log.error("Error uploading file to S3", e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public String uploadCollectionImage(Long collectionId, MultipartFile file, String imageType) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }
            // generate unique name 
            String fileName = generateFileName(collectionId, "COLLECTION", file.getOriginalFilename(), imageType);
            
            // upload on S3
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            
            s3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);

            // generated file URL on S3
            return generateImageUrl(fileName);
            
        } catch (IOException e) {
            log.error("Error uploading file to S3", e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public String uploadCategoryImage(Long categoryId, MultipartFile file, String imageType) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }
            // generate unique name 
            String fileName = generateFileName(categoryId, "CATEGORY", file.getOriginalFilename(), imageType);
            
            // upload on S3
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            
            s3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);

            // generated file URL on S3
            return generateImageUrl(fileName);
            
        } catch (IOException e) {
            log.error("Error uploading file to S3", e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public String uploadBrandImage(Long brandId, MultipartFile file, String imageType) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }
            // generate unique name 
            String fileName = generateFileName(brandId, "BRAND", file.getOriginalFilename(), imageType);
            
            // upload on S3
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            
            s3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);

            // generated file URL on S3
            return generateImageUrl(fileName);
            
        } catch (IOException e) {
            log.error("Error uploading file to S3", e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    private String generateImageUrl(String fileName) {
        // if (cloudFrontDomain != null && !cloudFrontDomain.isEmpty()) {
        //     // use CloudFront URL in production
        //     return String.format("https://%s/%s", cloudFrontDomain, fileName);
        // } else {
            return s3Client.getUrl(bucketName, fileName).toString();
        // }
    }

    public void deleteImage(String imageUrl) {
        try {
            // https://addmix-store-images.s3.amazonaws.com/products/28/gallery/1760907483673_99cd3e17.png
            // extract key from URL
            String key = extractKeyFromUrl(imageUrl);
            s3Client.deleteObject(bucketName, key);
        } catch (Exception e) {
            log.error("Error deleting image from S3: {}", imageUrl, e);
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    private String generateFileName(Long id, String folederName, String originalFileName, String imageType) {
        String extension = getFileExtension(originalFileName);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomId = UUID.randomUUID().toString().substring(0, 8);

        switch (folederName) {
            case "PRODUCT":
                return String.format("products/%d/%s/%s_%s%s", 
                    id, imageType, timestamp, randomId, extension);
            case "COLLECTION":
                return String.format("collections/%d/%s_%s%s", 
                    id, timestamp, randomId, extension);
            case "CATEGORY":
                return String.format("categories/%d/%s_%s%s", 
                    id, timestamp, randomId, extension);
            case "BRAND":
                return String.format("brands/%d/%s_%s%s", 
                    id, timestamp, randomId, extension);
            default:
                throw new IllegalArgumentException("Invalid folder name: " + folederName);
        }
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * Extract S3 key from URL
     * Handles both S3 and CloudFront URLs
     */
    private String extractKeyFromUrl(String url) {
        try {
            log.debug("Extracting key from URL: {}", url);
            
            // Handle CloudFront URL
            // if (cloudFrontDomain != null && !cloudFrontDomain.isEmpty() && url.contains(cloudFrontDomain)) {
            //     // CloudFront URL: https://d123.cloudfront.net/products/1/gallery/123_abc.jpg
            //     String key = url.replace(String.format("https://%s/", cloudFrontDomain), "");
            //     log.debug("Extracted key from CloudFront URL: {}", key);
            //     return key;
            // }
            
            // Handle S3 URL - multiple possible formats
            // Format 1: https://bucket-name.s3.region.amazonaws.com/products/1/gallery/123_abc.jpg
            // Format 2: https://bucket-name.s3.amazonaws.com/products/1/gallery/123_abc.jpg
            // Format 3: https://s3.region.amazonaws.com/bucket-name/products/1/gallery/123_abc.jpg
            
            // Try virtual-hosted-style URL first
            String virtualHostedPattern = String.format("https://%s.s3", bucketName);
            if (url.contains(virtualHostedPattern)) {
                // https://bucket.s3.region.amazonaws.com/key
                // or https://bucket.s3.amazonaws.com/key
                String key = url.replaceAll("https://" + bucketName + "\\.s3[^/]*\\.amazonaws\\.com/", "");
                log.debug("Extracted key from virtual-hosted-style URL: {}", key);
                return key;
            }
            
            // Try path-style URL
            String pathStylePattern = String.format("https://s3");
            if (url.contains(pathStylePattern)) {
                // https://s3.region.amazonaws.com/bucket/key
                // or https://s3.amazonaws.com/bucket/key
                String key = url.replaceAll("https://s3[^/]*\\.amazonaws\\.com/" + bucketName + "/", "");
                log.debug("Extracted key from path-style URL: {}", key);
                return key;
            }
            
            // Fallback: try to extract from the end of URL
            if (url.contains(bucketName)) {
                String[] parts = url.split(bucketName + "/");
                if (parts.length > 1) {
                    String key = parts[parts.length - 1].split("\\?")[0]; // remove query params
                    log.debug("Extracted key using fallback method: {}", key);
                    return key;
                }
            }
            
            throw new RuntimeException("Could not extract S3 key from URL: " + url);
        } catch (Exception e) {
            log.error("Error extracting key from URL: {}", url, e);
            throw new RuntimeException("Invalid S3 URL format: " + url, e);
        }
    }
}