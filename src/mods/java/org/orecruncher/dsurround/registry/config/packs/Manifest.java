package org.orecruncher.dsurround.registry.config.packs;

import com.google.gson.annotations.SerializedName;

public class Manifest {
   @SerializedName("version")
   protected int version = 0;
   @SerializedName("name")
   protected String name = "(unspecified)";
   @SerializedName("author")
   protected String author = "(unspecified)";
   @SerializedName("website")
   protected String website = "(unspecified)";

   public Manifest() {
   }

   public int getVersion() {
      return this.version;
   }

   public String getName() {
      return this.name != null ? this.name : "(unspecified)";
   }

   public String getAuthor() {
      return this.author != null ? this.author : "(unspecified)";
   }

   public String getWebsite() {
      return this.website != null ? this.website : "(unspecified)";
   }
}
