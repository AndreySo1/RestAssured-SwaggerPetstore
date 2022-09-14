package petstore.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import petstore.api.spec.Specification;
import petstore.api.spec.entities.DeletePetRequest;
import petstore.api.spec.entities.DeletePetResponse;
import petstore.api.spec.entities.Pet;
import petstore.api.spec.entities.PetLong;

import static io.restassured.RestAssured.given;

public class ApiTest {

   private final static String URL = "https://petstore.swagger.io/v2/";
   private Integer myId = 555;


   @DataProvider(name = "petStatus")
   public Object[][] petStatus(){
      return new Object[][]{
         {"available"},
         {"pending"},
         {"sold"}
      };
   }

   @Test
   public void addNewPetSuccessTest() {
      Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecCode(200)); 

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

      Assert.assertNotNull(myPet.getId());
      Assert.assertNotNull(myPet.getName());
      Assert.assertTrue(myPet.getPhotoUrls().size() != 0);

      Assert.assertEquals(myPet.getId(), expectedId);
      Assert.assertEquals(myPet.getName(), expectedName);
      Assert.assertEquals(myPet.getPhotoUrls(), expectedFotoUrl);
     
   }

   @Test
   public void updatePetSuccessTest() {      
      addNewPetSuccessTest();

      Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecCode(200));

      Random random = new Random();
      String expectedName = "updateName"+random.nextInt();

      given()
         .contentType("application/x-www-form-urlencoded; charset=utf-8")
         .formParam("petId", myId)
         .formParam("name", expectedName)
         .when()
         .post("pet/"+myId)
         .then().log().all();

      Pet responsePet = given()
         .when()
         .get("pet/"+ myId)
         .then().log().all()
         .extract().as(Pet.class);

      Assert.assertNotNull(responsePet.getName());
      Assert.assertEquals(responsePet.getName(), expectedName); 
   }

   @Test
   public void findsPetsByIdTest() {
      addNewPetSuccessTest();

      Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecCode(200));

      Pet response = given()
         .when()
         .get("pet/"+ myId)
         .then().log().all()
         .extract().as(Pet.class);
      
       Assert.assertEquals(response.getId(), myId);
   }

    @Test(dataProvider = "petStatus")
    public void findsPetsByStatusTest(String petStatus) {
      Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecCode(200)); 

      List<PetLong> pets = given()
         .when()
         .get("pet/findByStatus?status="+ petStatus)
         .then()
         .extract().body().jsonPath().getList("", PetLong.class);

      Assert.assertTrue(pets.stream().allMatch(x->x.getStatus().equals(petStatus)), "petStatus Not equals");
    }

   @Test
   public void deletePetSuccessTest(){
      addNewPetSuccessTest();

      Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecCode(200)); 

      DeletePetRequest request = new DeletePetRequest();
      request.setPetId(myId);

      DeletePetResponse response = given()
         .body(request)
         .when()
         .delete("pet/"+myId)
         .then().log().all()
         .extract().as(DeletePetResponse.class);

      Assert.assertNotNull(response.getMessage());
      Assert.assertEquals(response.getMessage(), myId.toString());
   }
}
