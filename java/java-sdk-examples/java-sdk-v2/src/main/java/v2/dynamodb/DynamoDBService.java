package v2.dynamodb;

import java.net.URI;
import java.text.ParseException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DynamoDBService {

  // credentials that can be replaced with real AWS values
  private static final String ACCESS_KEY = "test";
  private static final String SECRET_KEY = "test";
  private static String TABLE_NAME = "person";
  private static AwsCredentialsProvider credentials = StaticCredentialsProvider.create(
      AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY));

  // create the dynamoDB client using the credentials and specific region
  private static Region region = Region.US_EAST_1;

  // create a dynamoDB client
  private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
      .region(region)
      .credentialsProvider(
          credentials)
      .endpointOverride(URI.create("https://localhost.localstack.cloud:4566"))
      .build();

  public static void main(String[] args) throws ParseException {
    String personId = "000012356";

    addEntryToDynamoDB(personId);

    readEntryFromDynamoDB(personId);

  }

  private static void readEntryFromDynamoDB(String personId) {
    try {

      DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
          .dynamoDbClient(dynamoDbClient)
          .build();
      DynamoDbTable<Person> table = enhancedClient.table(TABLE_NAME,
          TableSchema.fromBean(Person.class));

      Person person = table.getItem(Key.builder().partitionValue(personId).build());

      // do something with the person object
      if (person != null) {
        System.out.println("Retrieved Person: " + person);
      } else {
        System.out.println("Person with ID " + personId + " not found.");
      }
    } catch (DynamoDbException exception) {
      System.out.println("Something happened: " + exception.getMessage());
    }
  }

  private static void addEntryToDynamoDB(String personID) throws ParseException {
    try {
      DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
          .dynamoDbClient(dynamoDbClient)
          .build();

      // create the Person object
      Person person = new Person();
      person.setId(personID);
      person.setName("John Doe");
      person.setBirthdateFromString("1979-01-01");

      // use the enhanced client to interact with the table
      DynamoDbTable<Person> table = enhancedClient.table(TABLE_NAME,
          TableSchema.fromBean(Person.class));
      table.putItem(person);

      System.out.println("Entry added successfully!");
    } catch (DynamoDbException exception) {
      System.out.println("An error occurred: " + exception.getMessage());
    }
  }
}
