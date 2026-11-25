package snownee.snow;

import java.util.Random;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class SnowGenerator implements IWorldGenerator {
   public SnowGenerator() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void blockVanilla(Populate event) {
      if (ModConfig.placeSnowInBlock && ModConfig.replaceSnowWorldGen && event.getType() == EventType.ICE) {
         event.setResult(Result.DENY);
      }
   }

   public static void trySnowAt(World world, BlockPos pos, boolean checkLight) {
      Biome biome = world.getBiome(pos);
      float f = biome.getTemperature(pos);
      if (f < 0.15F) {
         IBlockState state = world.getBlockState(pos);
         if (state.getBlock() != Blocks.SNOW_LAYER) {
            if (state.getBlock() == Blocks.AIR || ModSnowBlock.canContainState(state)) {
               if (pos.getY() >= 0 && world.getLightFor(EnumSkyBlock.BLOCK, pos) < 10) {
                  ModSnowBlock.placeLayersOn(world, pos, 1, false, false, 2);
               }

            }
         }
      }
   }

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (ModConfig.placeSnowInBlock && ModConfig.replaceSnowWorldGen) {
         boolean pre = BlockFalling.fallInstantly;
         BlockFalling.fallInstantly = true;

         for(int k2 = 0; k2 < 16; ++k2) {
            for(int j3 = 0; j3 < 16; ++j3) {
               BlockPos blockpos1 = world.getPrecipitationHeight(new BlockPos(chunkX * 16 + 8 + k2, 0, chunkZ * 16 + 8 + j3));
               BlockPos blockpos2 = blockpos1.down();
               if (world.canBlockFreezeWater(blockpos2)) {
                  world.setBlockState(blockpos2, Blocks.ICE.getDefaultState(), 2);
               }

               IBlockState state = world.getBlockState(blockpos2);
               if (ModSnowBlock.canContainState(state)) {
                  blockpos1 = blockpos2;
               }

               trySnowAt(world, blockpos1, true);
            }
         }

         BlockFalling.fallInstantly = pre;
      }
   }
}
