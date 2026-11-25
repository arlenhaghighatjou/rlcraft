package org.orecruncher.dsurround.registry.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public final class ModConfiguration {
   public String source = "<UNKNOWN>";
   @SerializedName("soundMetadata")
   public Map<String, SoundMetadataConfig> sounds = ImmutableMap.of();
   @SerializedName("biomes")
   public List<BiomeConfig> biomes = ImmutableList.of();
   @SerializedName("biomeAlias")
   public Map<String, String> biomeAlias = ImmutableMap.of();
   @SerializedName("blocks")
   public List<BlockConfig> blocks = ImmutableList.of();
   @SerializedName("dimensions")
   public List<DimensionConfig> dimensions = ImmutableList.of();
   @SerializedName("footsteps")
   public Map<String, String> footsteps = ImmutableMap.of();
   @SerializedName("footprints")
   public List<String> footprints = ImmutableList.of();
   @SerializedName("forgeMappings")
   public List<ModConfiguration.ForgeEntry> forgeMappings = ImmutableList.of();
   @SerializedName("items")
   public Map<String, List<String>> items = ImmutableMap.of();
   @SerializedName("variators")
   public Map<String, VariatorConfig> variators = ImmutableMap.of();
   @SerializedName("entities")
   public Map<String, EntityConfig> entities = ImmutableMap.of();
   @SerializedName("acoustics")
   public Map<String, JsonElement> acoustics = ImmutableMap.of();
   @SerializedName("primitiveAcoustics")
   public Map<String, String> primitiveAcoustics = ImmutableMap.of();

   public ModConfiguration() {
   }

   public static class ForgeEntry {
      @SerializedName("acousticProfile")
      public String acousticProfile = null;
      @SerializedName("dictionaryEntries")
      public List<String> dictionaryEntries = ImmutableList.of();

      public ForgeEntry() {
      }
   }
}
