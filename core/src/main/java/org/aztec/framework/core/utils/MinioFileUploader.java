package org.aztec.framework.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.aztec.framework.disconf.items.MiniosConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParserException;

import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidArgumentException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidExpiresRangeException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.NoResponseException;
import io.minio.errors.RegionConflictException;

@Component
public class MinioFileUploader {

    @Autowired
    MiniosConf miniosConf;

    public void uploadFile(String filePath, String objectName) throws InvalidKeyException, InvalidBucketNameException,
            NoSuchAlgorithmException, NoResponseException, ErrorResponseException, InternalException,
            InvalidArgumentException, InsufficientDataException, InvalidResponseException, RegionConflictException,
            InvalidEndpointException, InvalidPortException, IOException, XmlPullParserException {
        uploadFileByPath(miniosConf.getUrl(), miniosConf.getAppKey(), miniosConf.getAppSecret(), miniosConf.getBucket(),
                filePath, objectName);
    }

    public String getDownloadUrl(String objectName) throws InvalidKeyException, NumberFormatException,
            InvalidEndpointException, InvalidPortException, InvalidBucketNameException, NoSuchAlgorithmException,
            InsufficientDataException, NoResponseException, ErrorResponseException, InternalException,
            InvalidExpiresRangeException, InvalidResponseException, IOException, XmlPullParserException {
        return getDownloadUrl(miniosConf.getUrl(), miniosConf.getAppKey(), miniosConf.getAppSecret(),
                miniosConf.getBucket(), objectName, Integer.parseInt(miniosConf.getDownloadTimeout()));
    }
    
    public String getDownloadUrl(String bucket, String objectName, Integer expires) throws InvalidKeyException, NumberFormatException,
    InvalidEndpointException, InvalidPortException, InvalidBucketNameException, NoSuchAlgorithmException,
    InsufficientDataException, NoResponseException, ErrorResponseException, InternalException,
    InvalidExpiresRangeException, InvalidResponseException, IOException, XmlPullParserException {
        return getDownloadUrl(miniosConf.getUrl(), miniosConf.getAppKey(), miniosConf.getAppSecret(),
                bucket, objectName, expires);
}

    public static void uploadFileByPath(String url, String appKey, String secretKey, String bucket, String filePath,
            String targetPath) throws InvalidKeyException, InvalidBucketNameException, NoSuchAlgorithmException,
            NoResponseException, ErrorResponseException, InternalException, InvalidArgumentException,
            InsufficientDataException, InvalidResponseException, IOException, XmlPullParserException,
            RegionConflictException, InvalidEndpointException, InvalidPortException {

        MinioClient minioClient = new MinioClient(url, appKey, secretKey);
        boolean isExist = minioClient.bucketExists(bucket);
        if (!isExist) {
            minioClient.makeBucket(bucket);
        }

        minioClient.putObject(bucket, targetPath, filePath);
    }

    public static String getDownloadUrl(String url, String appKey, String secretKey, String bucketName,
            String ObjectName, Integer expires) throws InvalidEndpointException, InvalidPortException,
            InvalidKeyException, InvalidBucketNameException, NoSuchAlgorithmException, InsufficientDataException,
            NoResponseException, ErrorResponseException, InternalException, InvalidExpiresRangeException,
            InvalidResponseException, IOException, XmlPullParserException {

        MinioClient minioClient = new MinioClient(url, appKey, secretKey);
        return minioClient.presignedGetObject(bucketName, ObjectName, expires);
    }
    
    /**
     * 上传文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param stream     文件流
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
     */
    public void putObject(String bucketName, String objectName, InputStream stream) throws Exception {
        
        MinioClient minioClient = new MinioClient(miniosConf.getUrl(), miniosConf.getAppKey(), miniosConf.getAppSecret());
        minioClient.putObject(bucketName, objectName, stream, stream.available(), "application/octet-stream");
    }
    
    /**
     * 获取文件
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 二进制流
     * @throws XmlPullParserException 
     * @throws IOException 
     * @throws InvalidResponseException 
     * @throws InvalidArgumentException 
     * @throws InternalException 
     * @throws ErrorResponseException 
     * @throws NoResponseException 
     * @throws InsufficientDataException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidBucketNameException 
     * @throws InvalidKeyException 
     * @throws InvalidPortException 
     * @throws InvalidEndpointException 
     */
    public InputStream getObject(String bucketName, String objectName) throws InvalidKeyException, InvalidBucketNameException, NoSuchAlgorithmException, InsufficientDataException, NoResponseException, ErrorResponseException, InternalException, InvalidArgumentException, InvalidResponseException, IOException, XmlPullParserException, InvalidEndpointException, InvalidPortException {
        MinioClient minioClient = new MinioClient(miniosConf.getUrl(), miniosConf.getAppKey(), miniosConf.getAppSecret());
        return minioClient.getObject(bucketName, objectName);
    }

    public static void main(String[] args) {
        try {
            /*
             * uploadFileByPath("http://minio.bsmdev.itsjsc.com:8898/",
             * "minioAdmin", "=;xAq3Wt#wos1PN:", "bms",
             * "test/upload/elastic_job_main_config.properties",
             * "elastic_job_main_config.properties");
             */
            System.out.println(getDownloadUrl("http://minio.bsmdev.itsjsc.com:8898/", "minioAdmin", "=;xAq3Wt#wos1PN:",
                    "bms", "elastic_job_main_config.properties", 5000));
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }
    }
}
