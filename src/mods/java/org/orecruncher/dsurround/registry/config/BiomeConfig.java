package org.orecruncher.dsurround.registry.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public final class BiomeConfig {
   @SerializedName("biomeName")
   public String biomeName = null;
   @SerializedName("conditions")
   public String conditions = null;
   @SerializedName("_comment")
   public String comment = null;
   @SerializedName("precipitation")
   public Boolean hasPrecipitation = null;
   @SerializedName("dust")
   public Boolean hasDust = null;
   @SerializedName("aurora")
   public Boolean hasAurora = null;
   @SerializedName("fog")
   public Boolean hasFog = null;
   @SerializedName("dustColor")
   public String dustColor = null;
   @SerializedName("fogColor")
   public String fogColor = null;
   @SerializedName("fogDensity")
   public Float fogDensity = null;
   @SerializedName("soundReset")
   public Boolean soundReset = null;
   @SerializedName("spotSoundChance")
   public Integer spotSoundChance = null;
   @SerializedName("sounds")
   public List<SoundConfig> sounds = ImmutableList.of();

   public BiomeConfig() {
   }
}
