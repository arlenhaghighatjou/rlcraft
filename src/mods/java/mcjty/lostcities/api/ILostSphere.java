package mcjty.lostcities.api;

import javax.annotation.Nullable;
import mcjty.lostcities.varia.ChunkCoord;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public interface ILostSphere {
   ChunkCoord getCenter();

   BlockPos getCenterPos();

   float getRadius();

   @Nullable
   Biome getBiome();

   boolean isEnabled();
}
