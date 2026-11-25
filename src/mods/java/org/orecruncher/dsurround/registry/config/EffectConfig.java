package org.orecruncher.dsurround.registry.config;

import com.google.gson.annotations.SerializedName;

public final class EffectConfig {
   @SerializedName("effect")
   public String effect = null;
   @SerializedName("conditions")
   public String conditions = "";
   @SerializedName("chance")
   public Integer chance = null;

   public EffectConfig() {
   }
}
