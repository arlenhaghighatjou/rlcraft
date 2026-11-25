package net.silentchaos512.lib.world;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.IWorldGenerator;

public abstract class WorldGeneratorSL implements IWorldGenerator {
   public final boolean allowRetrogen;
   public final String retrogenKey;
   public final int retrogenVersion;
   protected boolean printDebugInfo;

   public WorldGeneratorSL(boolean allowRetrogren, String retrogenKey) {
      this(allowRetrogren, retrogenKey, 1);
   }

   public WorldGeneratorSL(boolean allowRetrogren, String retrogenKey, int retrogenVersion) {
      this.printDebugInfo = false;
      this.allowRetrogen = allowRetrogren;
      this.retrogenKey = retrogenKey;
      this.retrogenVersion = retrogenVersion > 0 ? retrogenVersion : 1;
      if (allowRetrogren) {
         MinecraftForge.EVENT_BUS.register(this);
      }

   }

   public void generate(EaglercraftRandom random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      int dim = world.provider.getDimension();
      int posX = chunkX * 16;
      int posZ = chunkZ * 16;
      if (!this.generateForDimension(dim, world, random, posX, posZ)) {
         switch(dim) {
         case -1:
            this.generateNether(world, random, posX, posZ);
            break;
         case 0:
            this.generateSurface(world, random, posX, posZ);
            break;
         case 1:
            this.generateEnd(world, random, posX, posZ);
            break;
         default:
            this.generateSurface(world, random, posX, posZ);
         }
      }

   }

   protected void generateSurface(World world, EaglercraftRandom random, int posX, int posZ) {
   }

   protected void generateNether(World world, EaglercraftRandom random, int posX, int posZ) {
   }

   protected void generateEnd(World world, EaglercraftRandom random, int posX, int posZ) {
   }

   protected boolean generateForDimension(int dim, World world, EaglercraftRandom random, int posX, int posZ) {
      return false;
   }
}
