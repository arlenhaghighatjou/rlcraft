package com.charles445.simpledifficulty.api.temperature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITemperatureModifier {
   float getPlayerInfluence(EntityPlayer var1);

   float getWorldInfluence(World var1, BlockPos var2);

   String getName();
}
