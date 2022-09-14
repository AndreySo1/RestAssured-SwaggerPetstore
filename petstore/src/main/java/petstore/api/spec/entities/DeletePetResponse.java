package petstore.api.spec.entities;

import lombok.Data;

@Data
public class DeletePetResponse {
   private Integer code;
   private String type;
   private String message;
}
