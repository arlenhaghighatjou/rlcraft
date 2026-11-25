package me.jellysquid.mods.phosphor.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;

public interface ILightingEngine {
   void scheduleLightUpdate(EnumSkyBlock var1, BlockPos var2);

   void processLightUpdates();

   void processLightUpdatesForType(EnumSkyBlock var1);
}
