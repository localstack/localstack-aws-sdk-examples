package v1.dynamodb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

  public class Person {
    private String id;
    private String name;
    private Date birthdate;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Date getBirthdate() {
      return birthdate;
    }

    public void setBirthdate(Date birthdate) {
      this.birthdate = birthdate;
    }

    public String getBirthdateAsString() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      return sdf.format(birthdate);
    }

    public void setBirthdateFromString(String birthdateStr) throws ParseException {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      this.birthdate = sdf.parse(birthdateStr);
    }

    @Override
    public String toString() {
      return "Person{" +
          "id='" + id + '\'' +
          ", name='" + name + '\'' +
          ", birthdate=" + birthdate +
          '}';
    }
  }
