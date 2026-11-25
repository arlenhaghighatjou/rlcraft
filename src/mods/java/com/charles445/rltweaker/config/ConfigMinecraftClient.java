package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;

public class ConfigMinecraftClient {
   @Comment({"REQUIRES Patch patchOverlayMessage. Whether overlay text should have a dropshadow"})
   @Name("Overlay Text Drop Shadow")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean overlayTextDropShadow = false;
   @Comment({"REQUIRES Patch patchOverlayMessage. Moves overlay text up by this amount (can be negative to move down)"})
   @Name("Overlay Text Offset")
   @RLConfig.ImprovementsOnly("0")
   @RLConfig.RLCraftTwoEightTwo("0")
   @RLConfig.RLCraftTwoNine("0")
   public int overlayTextOffset = 0;

   public ConfigMinecraftClient() {
   }
}
