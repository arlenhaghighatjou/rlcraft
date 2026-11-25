package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.temperature.ITemperatureDynamicModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ModifierDynamicBase implements ITemperatureDynamicModifier {
   private final String name;
   protected final float defaultTemperature;

   protected ModifierDynamicBase(String name) {
      this.name = name;
      this.defaultTemperature = (float)((TemperatureEnum.NORMAL.getUpperBound() + TemperatureEnum.COLD.getUpperBound()) / 2);
   }

   public float applyDynamicPlayerInfluence(EntityPlayer player, float currentTemperature) {
      return currentTemperature;
   }

   public float applyDynamicWorldInfluence(World world, BlockPos pos, float currentTemperature) {
      return currentTemperature;
   }

   public String getName() {
      return this.name;
   }
}
