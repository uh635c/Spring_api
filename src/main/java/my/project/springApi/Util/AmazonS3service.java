package my.project.springApi.Util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.amazonaws.services.s3.AmazonS3;

@Component
public class AmazonS3service {
    @Value("${access.key}")
    private String accessKey;
    @Value("${secret.key}")
    private String secretKey;

    public AmazonS3 getAmazonS3Client(){
        AWSCredentials credentials = new BasicAWSCredentials(
                accessKey,
                secretKey);

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();
    }



}
