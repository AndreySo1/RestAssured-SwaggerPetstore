package petstore.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import petstore.BaseApiLogTest;
import petstore.api.spec.Specification;
import petstore.api.spec.entities.DeletePetRequest;
import petstore.api.spec.entities.DeletePetResponse;
import petstore.api.spec.entities.Pet;
import petstore.api.spec.entities.PetLong;

import static io.restassured.RestAssured.given;

public class ApiTest extends BaseApiLogTest{

   private final static String URL = "https://petstore.swagger.io/v2/";
   private final Integer myId = 555;

   public static Logger logger = LoggerFactory.getLogger(ApiTest.class);

   @DataProvider(name = "petStatus")
   public Object[][] petStatus(){
      return new Object[][]{
         {"available"},
         {"pending"},
         {"sold"}
      };
   }

   @BeforeMethod
   public void beforeEachTest(){
        logger.info("---////////////////////////////////////---");
   }

   @Test
   public void addNewPetSuccessTest(){
      logger.trace("---Start test -addNewPetSuccessTest---");
      Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecCode(200));
      logger.trace("Status code - 200");

      Integer expectedId = myId;
      String expectedName = "testCatBarsik2";
      ArrayList<String> expectedFotoUrl = new ArrayList<>();
      expectedFotoUrl.add("testUrl");

      Pet expectedPet = new Pet();
      expectedPet.setId(expectedId);
      expectedPet.setName(expectedName);
      expectedPet.setPhotoUrls(expectedFotoUrl);

      Pet myPet = given()
         .body(expectedPet)
         .when()
         .post("pet")
         .then().log().all()
         .extract().as(Pet.class);
      logger.trace("response: {}", myPet);

      Assert.assertNotNull(myPet.getId());
      Assert.assertNotNull(myPet.getName());
      Assert.assertTrue(myPet.getPhotoUrls().size() != 0);

      Assert.assertEquals(myPet.getId(), expectedId);
      Assert.assertEquals(myPet.getName(), expectedName);
      Assert.assertEquals(myPet.getPhotoUrls(), expectedFotoUrl);
      logger.trace("---Finish -addNewPetSuccessTest finished ---");
   }

   @Test
   public void updatePetSuccessTest() {
      logger.trace("---Start test -updatePetSuccessTest---");
      addNewPetSuccessTest();

      Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecCode(200));
      logger.trace("Status code - 200");

      Random random = new Random();
      String expectedName = "updateName"+random.nextInt();

      given()
         .contentType("application/x-www-form-urlencoded; charset=utf-8")
         .formParam("petId", myId)
         .formParam("name", expectedName)
              .filter(new RequestLoggingFilter(requestCapture))
         .when()
         .post("pet/"+myId)
         .then().log().all();
       logger.info("request: {}", requestWriter);

      Pet responsePet = given()
         .when()
         .get("pet/"+ myId)
         .then().log().all()
         .extract().as(Pet.class);
      logger.trace("response: {}", responsePet);

      Assert.assertNotNull(responsePet.getName());
      Assert.assertEquals(responsePet.getName(), expectedName);
      logger.trace("---Finish -updatePetSuccessTest finished ---");
   }

   @Test
   public void findsPetsByIdTest(){
         logger.trace("---Start test -findsPetsByIdTest---");
         addNewPetSuccessTest();

         Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecCode(200));
         logger.trace("Status code - 200");

         Pet response = given()
                 .when()
                 .filter(new ResponseLoggingFilter(responseCapture))
                 .get("pet/"+ myId)
                 .then().log().all()
                 .extract().as(Pet.class);
        logger.info("response: {}", responseWriter);

         Assert.assertEquals(response.getId(), myId);
         logger.trace("---Finish -findsPetsByIdTest finished ---");
   }

    @Test(dataProvider = "petStatus")
    public void findsPetsByStatusTest(String petStatus) {
      logger.trace("---Start test -findsPetsByStatusTest - status = {}---", petStatus);
      Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecCode(200));
      logger.trace("Status code - 200");

      List<PetLong> pets = given()
         .when()
         .get("pet/findByStatus?status="+ petStatus)
         .then()
         .extract().body().jsonPath().getList("", PetLong.class);

      Assert.assertTrue(pets.stream().allMatch(x->x.getStatus().equals(petStatus)), "petStatus Not equals");
      logger.trace("---Finish Start test -findsPetsByStatusTest finished ---");
    }

   @Test
   public void deletePetSuccessTest(){
      logger.trace("---Start test -deletePetSuccessTest---");
      addNewPetSuccessTest();

      Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecCode(200));
      logger.trace("Status code - 200");

      DeletePetRequest request = new DeletePetRequest();
      request.setPetId(myId);

      DeletePetResponse response = given()
         .body(request)
         .when()
         .delete("pet/"+myId)
         .then().log().all()
         .extract().as(DeletePetResponse.class);
      logger.trace("response: {}", response);

      Assert.assertNotNull(response.getMessage());
      Assert.assertEquals(response.getMessage(), myId.toString());
      logger.trace("---Finish -deletePetSuccessTest finished ---");
   }
}
