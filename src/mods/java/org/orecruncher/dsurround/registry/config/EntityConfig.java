package org.orecruncher.dsurround.registry.config;

import com.google.gson.annotations.SerializedName;

public class EntityConfig {
   @SerializedName("effects")
   public String effects = "";
   @SerializedName("variator")
   public String variator = "default";

   public EntityConfig() {
   }
}
