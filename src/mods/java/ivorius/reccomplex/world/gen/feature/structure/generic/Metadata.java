package ivorius.reccomplex.world.gen.feature.structure.generic;

import com.google.gson.annotations.SerializedName;

public class Metadata {
   @SerializedName("authors")
   public String authors = "";
   @SerializedName("weblink")
   public String weblink = "";
   @SerializedName("comment")
   public String comment = "";

   public Metadata() {
   }
}
