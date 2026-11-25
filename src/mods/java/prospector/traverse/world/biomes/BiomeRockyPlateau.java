package prospector.traverse.world.biomes;

import java.util.Random;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import prospector.traverse.config.TraverseConfig;
import prospector.traverse.world.WorldGenConstants;
import prospector.traverse.world.features.WorldGenPatch;

public class BiomeRockyPlateau extends Biome implements WorldGenConstants {
   protected static final WorldGenPatch COARSE_DIRT_PATCH_FEATURE = new WorldGenPatch(Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.COARSE_DIRT), 4);
   protected static final WorldGenPatch STONE_PATCH_FEATURE = new WorldGenPatch(Blocks.STONE.getDefaultState(), 5);
   protected static final WorldGenBlockBlob COBBLESTONE_BOULDER_FEATURE = new WorldGenBlockBlob(Blocks.COBBLESTONE, 1);
   public static BiomeProperties properties = new BiomeProperties("Rocky Plateau");

   public BiomeRockyPlateau() {
      super(properties);
      this.decorator.treesPerChunk = 0;
      this.decorator.extraTreeChance = 0.0F;
      this.decorator.flowersPerChunk = 0;
      this.decorator.grassPerChunk = 8;
      this.spawnableCreatureList.clear();
   }

   public void decorate(World worldIn, Random rand, BlockPos pos) {
      int coarseDirtChance = rand.nextInt(7);
      if (coarseDirtChance == 0) {
         int k6 = rand.nextInt(16) + 8;
         int l = rand.nextInt(16) + 8;
         BlockPos blockpos = worldIn.getHeight(pos.add(k6, 0, l));
         COARSE_DIRT_PATCH_FEATURE.generate(worldIn, rand, blockpos);
      }

      int stoneChance = rand.nextInt(9);
      if (stoneChance == 0) {
         int k6 = rand.nextInt(16) + 8;
         int l = rand.nextInt(16) + 8;
         BlockPos blockpos = worldIn.getHeight(pos.add(k6, 0, l));
         STONE_PATCH_FEATURE.generate(worldIn, rand, blockpos);
      }

      if (!TraverseConfig.disallowBoulders && TerrainGen.decorate(worldIn, rand, pos, EventType.ROCK)) {
         int genChance = rand.nextInt(3);
         if (genChance == 0) {
            int k6 = rand.nextInt(16) + 8;
            int l = rand.nextInt(16) + 8;
            BlockPos blockpos = worldIn.getHeight(pos.add(k6, 0, l));
            COBBLESTONE_BOULDER_FEATURE.generate(worldIn, rand, blockpos);
         }
      }

      super.decorate(worldIn, rand, pos);
   }

   static {
      properties.setTemperature(0.8F);
      properties.setRainfall(0.2F);
      properties.setBaseHeight(1.6F);
      properties.setHeightVariation(0.0F);
   }
}
