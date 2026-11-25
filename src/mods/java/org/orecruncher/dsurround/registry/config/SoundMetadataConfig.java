package org.orecruncher.dsurround.registry.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SoundMetadataConfig {
   @SerializedName("category")
   public String category = "neutral";
   @SerializedName("title")
   public String title = null;
   @SerializedName("credits")
   public List<String> credits = ImmutableList.of();

   public SoundMetadataConfig() {
   }
}
