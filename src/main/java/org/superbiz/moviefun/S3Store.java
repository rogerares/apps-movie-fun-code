package org.superbiz.moviefun;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class S3Store implements BlobStore{

    private final AmazonS3 s3;
    private final String bucketName;

    public S3Store(AmazonS3Client s3, String bucketName) {
        this.s3 = s3;
        this.bucketName = bucketName;
    }

    @Override
    public void put(Blob blob) throws IOException {
        s3.putObject(bucketName, blob.name, blob.inputStream, new ObjectMetadata());
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        Boolean objectExists = s3.doesObjectExist(bucketName, name);

        if(!objectExists) {
            return Optional.empty();
        } else {
            S3Object object = s3.getObject(bucketName, name);

            return Optional.of(new Blob(
                    name,
                    object.getObjectContent(),
                    object.getObjectMetadata().getContentType()
            ));
        }
    }

    @Override
    public void deleteAll() {
        List<S3ObjectSummary> summaries = s3
                .listObjects(bucketName)
                .getObjectSummaries();

        for (S3ObjectSummary summary : summaries) {
            s3.deleteObject(bucketName, summary.getKey());
        }
    }
}
