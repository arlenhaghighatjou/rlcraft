package com.charles445.simpledifficulty.api.temperature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITemperatureDynamicModifier {
   float applyDynamicPlayerInfluence(EntityPlayer var1, float var2);

   float applyDynamicWorldInfluence(World var1, BlockPos var2, float var3);

   String getName();
}
