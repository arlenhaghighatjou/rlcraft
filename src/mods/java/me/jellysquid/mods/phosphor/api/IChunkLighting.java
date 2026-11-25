package me.jellysquid.mods.phosphor.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;

public interface IChunkLighting {
   int getCachedLightFor(EnumSkyBlock var1, BlockPos var2);
}
