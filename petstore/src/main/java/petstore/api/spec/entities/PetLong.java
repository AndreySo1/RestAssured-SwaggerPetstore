package petstore.api.spec.entities;

import java.util.ArrayList;

import lombok.Data;

@Data
public class PetLong {
   private Long id;
   private Category category;
   private String name;
   private ArrayList<String> photoUrls;
   private ArrayList<Tag> tags;
   private String status;
}
