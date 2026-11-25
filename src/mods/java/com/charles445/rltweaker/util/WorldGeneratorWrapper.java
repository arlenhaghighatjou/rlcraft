package com.charles445.rltweaker.util;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public abstract class WorldGeneratorWrapper implements IWorldGenerator {
   private IWorldGenerator wrappedGenerator;

   public WorldGeneratorWrapper() {
   }

   public void setWrappedGenerator(IWorldGenerator generator) {
      this.wrappedGenerator = generator;
   }

   public IWorldGenerator getWrappedGenerator() {
      return this.wrappedGenerator;
   }

   public void generateWrapped(EaglercraftRandom random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (this.wrappedGenerator == null) {
         ErrorUtil.logSilent("WorldGeneratorWrapper generateWrapped null");
      } else {
         this.wrappedGenerator.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
      }

   }

   public abstract void generate(EaglercraftRandom var1, int var2, int var3, World var4, IChunkGenerator var5, IChunkProvider var6);
}
