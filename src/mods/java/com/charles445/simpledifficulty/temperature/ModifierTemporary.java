package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.temperature.TemporaryModifier;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.entity.player.EntityPlayer;

public class ModifierTemporary extends ModifierBase {
   public ModifierTemporary() {
      super("Temporary");
   }

   public float getPlayerInfluence(EntityPlayer player) {
      float sum = 0.0F;

      TemporaryModifier tm;
      for(UnmodifiableIterator var3 = SDCapabilities.getTemperatureData(player).getTemporaryModifiers().values().iterator(); var3.hasNext(); sum += tm.temperature) {
         tm = (TemporaryModifier)var3.next();
      }

      return sum;
   }
}
