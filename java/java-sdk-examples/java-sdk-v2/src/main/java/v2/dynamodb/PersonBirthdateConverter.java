package v2.dynamodb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class PersonBirthdateConverter implements AttributeConverter<Date> {

  @Override
  public AttributeValue transformFrom(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    return AttributeValue.fromS(sdf.format(date));
  }

  @Override
  public Date transformTo(AttributeValue attributeValue) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    try {
      return sdf.parse(attributeValue.s());
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public EnhancedType<Date> type() {
    return EnhancedType.of(Date.class);
  }

  @Override
  public AttributeValueType attributeValueType() {
    return AttributeValueType.S;
  }
}
