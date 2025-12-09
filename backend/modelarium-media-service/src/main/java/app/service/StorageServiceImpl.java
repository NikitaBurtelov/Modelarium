package app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    private final S3Client s3Client;

    @Override
    public PutObjectResponse putObject(PutObjectRequest request, RequestBody body) {
        return s3Client.putObject(request, body);
    }

    @Override
    public PutObjectResponse putObject(String bucket, String key, String contentType, InputStream stream) throws IOException {
        return s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromInputStream(stream, stream.available())
        );
    }

    @Override
    public ResponseInputStream<GetObjectResponse> getObject(GetObjectRequest request) {
        return s3Client.getObject(request);
    }

    @Override
    public DeleteObjectResponse deleteObject(DeleteObjectRequest request) {
        return s3Client.deleteObject(request);
    }
}
