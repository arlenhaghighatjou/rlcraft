package com.lycanitesmobs.core.worldgen.fluid;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.IWorldGenBase;
import com.lycanitesmobs.core.config.ConfigSpawning;
import com.lycanitesmobs.core.info.ModInfo;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenLakes;

public class WorldGenMoglavaLakes extends WorldGenLakes implements IWorldGenBase {
   public String name = "Moglava Lakes";
   public ModInfo group = LycanitesMobs.modInfo;
   public double generateUndergroundChance = 0.04D;
   public double generateSurfaceChance = 0.0D;
   public String dimensionEntries = "-1";
   public int[] dimensionBlacklist;
   public String[] dimensionTypes;
   public boolean dimensionWhitelist = true;

   public WorldGenMoglavaLakes() {
      super(ObjectManager.getBlock("moglava"));
      ConfigSpawning config = ConfigSpawning.getConfig(this.group, "worldgen");
      config.setCategoryComment("WorldGen Chances", "The chance that each worldgen will generate. You can set this to 0 to disable the worldgen or use the dimension black/white list. 1 = all over, 0.04 fairly rare.");
      this.generateUndergroundChance = config.getDouble("WorldGen Chances", this.name + " Underground Chance", this.generateUndergroundChance);
      this.generateSurfaceChance = config.getDouble("WorldGen Chances", this.name + " Surface Chance", this.generateSurfaceChance, "Moglava only generates in the Nether by default so this defaults to 0.");
      ConfigSpawning.SpawnDimensionSet dimensions = config.getDimensions("WorldGen Dimensions", this.name + " Dimensions", this.dimensionEntries);
      this.dimensionBlacklist = dimensions.dimensionIDs;
      this.dimensionTypes = dimensions.dimensionTypes;
      this.dimensionWhitelist = config.getBool("WorldGen Dimensions", this.name + " Dimensions Whitelist Mode", this.dimensionWhitelist);
   }

   public void onWorldGen(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (this.isValidDimension(world)) {
         if (this.generateSurfaceChance > 0.0D && (this.generateSurfaceChance >= 1.0D || random.nextDouble() <= this.generateSurfaceChance)) {
            int x = chunkX * 16 + 16;
            int z = chunkZ * 16 + 16;
            int y = random.nextInt(128);
            this.generate(world, random, new BlockPos(x, y, z));
         }

         if (this.generateUndergroundChance > 0.0D && (this.generateUndergroundChance >= 1.0D || random.nextDouble() <= this.generateUndergroundChance)) {
            int x = chunkX * 16 + 16;
            int z = chunkZ * 16 + 16;
            int top = Math.max(1, world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY() - 10);
            if (top > 0) {
               int y = random.nextInt(top);
               this.generate(world, random, new BlockPos(x, y, z));
            }
         }

      }
   }

   public boolean generate(World world, Random random, BlockPos pos) {
      return super.generate(world, random, pos);
   }

   public boolean isValidDimension(World world) {
      boolean validDimension = false;

      for(String eventDimensionType : this.dimensionTypes) {
         if ("ALL".equalsIgnoreCase(eventDimensionType)) {
            validDimension = true;
         } else if ("VANILLA".equalsIgnoreCase(eventDimensionType)) {
            validDimension = world.provider.getDimension() > -2 && world.provider.getDimension() < 2;
         }
      }

      if (!validDimension) {
         validDimension = !this.dimensionWhitelist;

         for(int eventDimension : this.dimensionBlacklist) {
            if (world.provider.getDimension() == eventDimension) {
               validDimension = this.dimensionWhitelist;
               break;
            }
         }
      }

      return validDimension;
   }
}
