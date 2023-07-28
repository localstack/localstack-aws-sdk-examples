package v1.s3;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class S3Service {

  // credentials that can be replaced with real AWS values
  private static final String ACCESS_KEY = "test";
  private static final String SECRET_KEY = "test";
  private static final String BUCKET_NAME = "records";

  // create BasicAWSCredentials using the access key and secret key
  private static BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);

  // create the amazon s3 client using the credentials and specific region
  private static AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
      .withCredentials(new AWSStaticCredentialsProvider(credentials))
      .withEndpointConfiguration(new EndpointConfiguration("s3.localhost.localstack.cloud:4566",
          Regions.US_EAST_1.getName()))
      .build();


  public static void main(String[] args) throws IOException {

    String key = "hello-v11.txt"; // the key for the file in the bucket
    String filePath = "java-sdk-v1/src/main/resources/hello-v1.txt"; // the path to the text file in the same folder

    addObjectToS3Bucket(key, filePath);

    readTextFileFromS3Bucket(key);

  }

  private static void readTextFileFromS3Bucket(String key) throws IOException {
    try {
      S3Object s3Object = s3Client.getObject(BUCKET_NAME, key);

      // read the text content of the file
      S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
      BufferedReader reader = new BufferedReader(new InputStreamReader(objectInputStream));

      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }

      // close the streams
      objectInputStream.close();
      reader.close();
    }
    catch (AmazonS3Exception s3Exception) {
      System.out.println("An error occurred: " + s3Exception.getMessage());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void addObjectToS3Bucket(String key, String filePath)
      throws FileNotFoundException {
    try {
      // read the content of the file from the local file system
      File file = new File(filePath);
      InputStream fileInputStream = new FileInputStream(file);

      // create ObjectMetadata to specify the content type and content length
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentType("text/plain");
      metadata.setContentLength(file.length());

      // put the text file into the S3 bucket
      s3Client.putObject(new PutObjectRequest(BUCKET_NAME, key, fileInputStream, metadata));

//      also an option, depending on how you handle your file
//      s3Client.putObject(new PutObjectRequest(BUCKET_NAME, key, file));

      System.out.println("Text file uploaded successfully!");

      fileInputStream.close();
    }
   catch (AmazonS3Exception s3Exception) {
    System.out.println("An error occurred: " + s3Exception.getMessage());
  } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
