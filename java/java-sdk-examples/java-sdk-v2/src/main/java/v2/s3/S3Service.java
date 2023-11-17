package v2.s3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Paths;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class S3Service {

  // credentials that can be replaced with real AWS values
  private static final String ACCESS_KEY = "test";
  private static final String SECRET_KEY = "test";

  private static Region region = Region.US_EAST_1;

  private static final String BUCKET_NAME = "records";
  // create an S3 client
  private static S3Client s3Client = S3Client.builder()
      .endpointOverride(URI.create("https://s3.localhost.localstack.cloud:4566"))
      .credentialsProvider(StaticCredentialsProvider.create(
          AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
      .region(region)
      .build();


  public static void main(String[] args) {

    // local file to upload
    String filePath = "java-sdk-v2/src/main/resources/hello-v2.txt";
    String objectKey = "hello-v2.txt";

    addObjectToS3Bucket(objectKey, filePath);

    readTextFileFromS3Bucket(objectKey);

    s3Client.close();
  }

  private static void readTextFileFromS3Bucket(String objectKey) {
    try {
      // get the object from the S3 bucket
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(BUCKET_NAME)
          .key(objectKey)
          .build();

      ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);

      // read the text content from the file
      String objectText = readResponseInputStream(response);

      System.out.println("Object text: " + objectText);

    } catch (S3Exception | IOException e) {
      System.err.println("Error reading object from S3: " + e.getMessage());
    }
  }

  private static void addObjectToS3Bucket(String objectKey, String filePath) {
    try {
      // put the object into the S3 bucket
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(BUCKET_NAME)
          .key(objectKey)
          .build();

      PutObjectResponse response = s3Client.putObject(putObjectRequest, Paths.get(filePath));

      if (response != null) {
        System.out.println("Object uploaded successfully!");
      } else {
        System.out.println("Object upload failed!");
      }
    } catch (S3Exception s3Exception) {
      System.out.println("An error occurred: " + s3Exception.getMessage());
    }
  }

  private static String readResponseInputStream(
      ResponseInputStream<GetObjectResponse> responseInputStream)
      throws IOException {
    try (InputStream inputStream = responseInputStream;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

      StringBuilder stringBuilder = new StringBuilder();
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        stringBuilder.append(line);
      }
      return stringBuilder.toString();
    }
  }
}

