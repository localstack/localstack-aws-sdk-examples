package v1.dynamodb;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import java.text.ParseException;

public class DynamoDBService {

  // credentials that can be replaced with real AWS values
  private static final String ACCESS_KEY = "test";
  private static final String SECRET_KEY = "test";
  private static String TABLE_NAME = "person";
  private static BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);

  // create the dynamoDB client using the credentials and specific region
  private static AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.standard()
      .withCredentials(new AWSStaticCredentialsProvider(credentials))
      .withEndpointConfiguration(
          new EndpointConfiguration("localhost.localstack.cloud:4566", Regions.US_EAST_1.getName()))
      .build();

  // create a dynamoDB instance
  private static DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);

  public static void main(String[] args) throws ParseException {
    String personId = "000012356";

    addEntryToDynamoDB(personId);

    readEntryFromDynamoDB(personId);

  }

  private static void readEntryFromDynamoDB(String personId) throws ParseException {
    Table table = dynamoDB.getTable(TABLE_NAME);

    String partitionKey = "id";

    // get the item from the dynamoDB table using the primary key
    Item item = table.getItem(partitionKey, personId);

    // map the dynamoDB item to a Person object
    Person person = new Person();
    person.setId(item.getString("id"));
    person.setName(item.getString("name"));
    person.setBirthdateFromString(item.getString("birthdate"));

    System.out.println(person.toString());
  }

  private static void addEntryToDynamoDB(String personID) throws ParseException {
    Table table = dynamoDB.getTable(TABLE_NAME);

    Person person = new Person();
    person.setId(personID);
    person.setName("John Doe");
    person.setBirthdateFromString("1984-10-12");

    // create an Item to represent the person object
    Item item = new Item()
        .withPrimaryKey("id", person.getId())
        .withString("name", person.getName())
        .withString("birthdate", person.getBirthdateAsString());

    // add the item to the dynamoDB table
    table.putItem(item);

    System.out.println("Person added successfully!");

  }
}
