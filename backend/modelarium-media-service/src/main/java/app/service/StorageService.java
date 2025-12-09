package app.service;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;

public interface StorageService {
    PutObjectResponse putObject(PutObjectRequest request, RequestBody body);
    PutObjectResponse putObject(String bucket, String key, String contentType, InputStream stream) throws IOException;
    ResponseInputStream<GetObjectResponse> getObject(GetObjectRequest request);
    DeleteObjectResponse deleteObject(DeleteObjectRequest request);
}
