package com.lycanitesmobs.core.worldgen;

import com.lycanitesmobs.core.IWorldGenBase;
import com.lycanitesmobs.core.worldgen.fluid.WorldGenAcidLakes;
import com.lycanitesmobs.core.worldgen.fluid.WorldGenMoglavaLakes;
import com.lycanitesmobs.core.worldgen.fluid.WorldGenOozeLakes;
import com.lycanitesmobs.core.worldgen.fluid.WorldGenPoisonLakes;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGeneratorFluids implements IWorldGenerator {
   protected final IWorldGenBase oozeLakes = new WorldGenOozeLakes();
   protected final IWorldGenBase poisonLakes = new WorldGenPoisonLakes();
   protected final IWorldGenBase acidLakes = new WorldGenAcidLakes();
   protected final IWorldGenBase moglavaLakes = new WorldGenMoglavaLakes();

   public WorldGeneratorFluids() {
   }

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      this.oozeLakes.onWorldGen(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
      this.poisonLakes.onWorldGen(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
      this.acidLakes.onWorldGen(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
      this.moglavaLakes.onWorldGen(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
   }
}
