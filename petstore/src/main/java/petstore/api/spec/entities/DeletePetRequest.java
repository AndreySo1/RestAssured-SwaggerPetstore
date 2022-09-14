package petstore.api.spec.entities;

import lombok.Data;

@Data
public class DeletePetRequest {
   private String api_key;
   private Integer petId;
}
