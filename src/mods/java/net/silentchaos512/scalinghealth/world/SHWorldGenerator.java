package net.silentchaos512.scalinghealth.world;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.silentchaos512.lib.world.WorldGeneratorSL;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.init.ModBlocks;

public class SHWorldGenerator extends WorldGeneratorSL {
   public SHWorldGenerator(boolean allowRetrogren) {
      super(allowRetrogren, "scalinghealth_retrogen");
   }

   public void generateSurface(World world, EaglercraftRandom random, int posX, int posZ) {
      Block block = ModBlocks.crystalOre;
      IBlockState state = block.getDefaultState();
      float trueVeinCount = Config.HEART_CRYSTAL_ORE_VEIN_COUNT;
      int spawnX = world.getSpawnPoint().getX() / 16;
      int spawnZ = world.getSpawnPoint().getZ() / 16;
      int chunkX = posX / 16;
      int chunkZ = posZ / 16;
      int deltaX = chunkX - spawnX;
      int deltaZ = chunkZ - spawnZ;
      float distance = (float)Math.sqrt((double)(deltaX * deltaX + deltaZ * deltaZ));
      trueVeinCount = trueVeinCount + Math.min(distance * Config.HEART_CRYSTAL_ORE_EXTRA_VEIN_RATE, Config.HEART_CRYSTAL_ORE_EXTRA_VEIN_CAP);
      int veinCount = (int)trueVeinCount;
      if (random.nextFloat() < Config.HEART_CRYSTAL_ORE_VEIN_COUNT - (float)veinCount) {
         ++veinCount;
      }

      int veinSize = Config.HEART_CRYSTAL_ORE_VEIN_SIZE;
      int minHeight = Config.HEART_CRYSTAL_ORE_MIN_HEIGHT;
      int maxHeight = Config.HEART_CRYSTAL_ORE_MAX_HEIGHT;

      for(int i = 0; i < veinCount; ++i) {
         int x = posX + random.nextInt(16);
         int y = random.nextInt(maxHeight - minHeight) + minHeight;
         int z = posZ + random.nextInt(16);
         BlockPos pos = new BlockPos(x, y, z);
         (new WorldGenMinable(state, veinSize)).generate(world, random, pos);
      }

   }

   protected void generateNether(World world, EaglercraftRandom random, int posX, int posZ) {
   }

   protected void generateEnd(World world, EaglercraftRandom random, int posX, int posZ) {
   }
}
