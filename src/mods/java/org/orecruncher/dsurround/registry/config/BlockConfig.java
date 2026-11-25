package org.orecruncher.dsurround.registry.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BlockConfig {
   @SerializedName("blocks")
   public List<String> blocks = ImmutableList.of();
   @SerializedName("soundReset")
   public Boolean soundReset = null;
   @SerializedName("effectReset")
   public Boolean effectReset = null;
   @SerializedName("chance")
   public Integer chance = null;
   @SerializedName("sounds")
   public List<SoundConfig> sounds = ImmutableList.of();
   @SerializedName("effects")
   public List<EffectConfig> effects = ImmutableList.of();

   public BlockConfig() {
   }
}
