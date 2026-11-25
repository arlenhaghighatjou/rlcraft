package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.entity.player.EntityPlayer;

public class ModifierSprint extends ModifierBase {
   public ModifierSprint() {
      super("Sprint");
   }

   public float getPlayerInfluence(EntityPlayer player) {
      return player.isSprinting() ? (float)ModConfig.server.temperature.sprintingValue : 0.0F;
   }
}
