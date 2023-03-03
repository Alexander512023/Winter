package com.goryaninaa.winter.web.http.server.json.deserializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.goryaninaa.winter.web.http.server.exception.ClientException;
import com.goryaninaa.winter.web.http.server.exception.ServerException;
import com.goryaninaa.winter.web.http.server.json.JsonDeserializer;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JsonDeserializer JUnit test class.
 *
 * @author Alex Goryanin
 */
class JsonDeserializerTest {

  private String correctUsersJson;
  private String correctPersonJson;
  private String incorrectDataJson;
  private static final String EMPTY_JSOM = "";
  private final JsonDeserializer deserializer = new JsonDeserializer();

  @BeforeEach
  public void init() {
    createCorrectReqresListUsersJson();
    createCorrectPersonJson();
    createIncorrectDataJson();
  }

  @Test
  void testDeserializeListAndFieldObject() {
    final Person person1 = new Person(7, "michael.lawson@reqres.in", "Michael", "Lawson",
        "https://reqres.in/img/faces/7-image.jpg");
    final Person person2 = new Person(8, "lindsay.ferguson@reqres.in", "Lindsay", "Ferguson",
        "https://reqres.in/img/faces/8-image.jpg");
    final Person person3 = new Person(9, "tobias.funke@reqres.in", "Tobias", "Funke",
        "https://reqres.in/img/faces/9-image.jpg");
    final List<Person> dataList = new ArrayList<>();
    dataList.add(person1);
    dataList.add(person2);
    dataList.add(person3);
    final Support support = new Support("https://reqres.in/#support-heading", "ReqRes");
    final ReqresListUsers actualUsersJson = new ReqresListUsers(2, 6, 12, 2, dataList, support);
    final ReqresListUsers expectedUsersJson = deserializer.deserialize(ReqresListUsers.class,
        correctUsersJson);
    assertEquals(expectedUsersJson, actualUsersJson);
  }

  @Test
  void testDeserialize() {
    final Person actualPersJson = new Person(7, "michael.lawson@reqres.in", "Michael", "Lawson",
        "https://reqres.in/img/faces/7-image.jpg");
    final Person expectedPersJson = deserializer.deserialize(Person.class, correctPersonJson);
    assertEquals(expectedPersJson, actualPersJson);
  }

  @Test
  void testDeserializeCheckIncorrect() {
    final Exception incorrectE = assertThrows(ClientException.class, () ->
      deserializer.deserialize(Person.class, incorrectDataJson));
    final String expectedMessage = "Deserializing JSON incorrect format";
    final String actualMessage = incorrectE.getMessage();
    assertEquals(expectedMessage, actualMessage);
  }

  @Test
  void testDeserializeCheckEmpty() {
    final Exception incorrectE = assertThrows(ServerException.class, () ->
            deserializer.deserialize(Person.class, EMPTY_JSOM));
    final String expectedMessage = "Empty JSON string";
    final String actualMessage = incorrectE.getMessage();
    assertEquals(expectedMessage, actualMessage);
  }

  private void createCorrectReqresListUsersJson() {
    correctUsersJson = "{\n" + "    \"page\": 2,\n" + "    \"per_page\": 6,\n"
        + "    \"total\": 12,\n" + "    \"total_pages\": 2,\n" + "    \"data\": [\n" + "        {\n"
        + "            \"id\": 7,\n" + "            \"email\": \"michael.lawson@reqres.in\",\n"
        + "            \"first_name\": \"Michael\",\n" + "            \"last_name\": \"Lawson\",\n"
        + "            \"avatar\": \"https://reqres.in/img/faces/7-image.jpg\"\n" + "        },\n"
        + "        {\n" + "            \"id\": 8,\n"
        + "            \"email\": \"lindsay.ferguson@reqres.in\",\n"
        + "            \"first_name\": \"Lindsay\",\n"
        + "            \"last_name\": \"Ferguson\",\n"
        + "            \"avatar\": \"https://reqres.in/img/faces/8-image.jpg\"\n" + "        },\n"
        + "        {\n" + "            \"id\": 9,\n"
        + "            \"email\": \"tobias.funke@reqres.in\",\n"
        + "            \"first_name\": \"Tobias\",\n" + "            \"last_name\": \"Funke\",\n"
        + "            \"avatar\": \"https://reqres.in/img/faces/9-image.jpg\"\n" + "        }\n"
        + "    ],\n" + "    \"support\": {\n"
        + "        \"url\": \"https://reqres.in/#support-heading\",\n"
        + "        \"text\": \"ReqRes\"\n" + "    }\n" + "}";
  }

  private void createCorrectPersonJson() {
    correctPersonJson = "{\n" + "            \"id\": 7,\n"
        + "            \"email\": \"michael.lawson@reqres.in\",\n"
        + "            \"first_name\": \"Michael\",\n" + "            \"last_name\": \"Lawson\",\n"
        + "            \"avatar\": \"https://reqres.in/img/faces/7-image.jpg\"\n" + "        }";
  }

  private void createIncorrectDataJson() {
    incorrectDataJson = "{\n" + "            \"id\": 7,\n"
        + "            \"email\": \"michael.lawson@reqres.in\",\n"
        + "            \"address\": \"Carl Marks 4E\"\n" + "        ";
  }

}
