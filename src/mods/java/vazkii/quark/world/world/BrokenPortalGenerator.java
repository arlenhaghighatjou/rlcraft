package vazkii.quark.world.world;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.arl.util.RotationHandler;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.world.feature.BrokenNetherPortals;

public class BrokenPortalGenerator implements IWorldGenerator {
   private final DimensionConfig dims;

   public BrokenPortalGenerator(DimensionConfig dims) {
      this.dims = dims;
   }

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (this.dims.canSpawnHere(world) && random.nextDouble() <= BrokenNetherPortals.chance) {
         int x = chunkX * 16 + 8 + random.nextInt(16);
         int z = chunkZ * 16 + 8 + random.nextInt(16);
         int y = random.nextInt(100) + 20;
         BlockPos pos = new BlockPos(x, y, z);

         while(world.isAirBlock(pos)) {
            pos = pos.down();
            if (pos.getY() < 10) {
               return;
            }

            if (world.isBlockFullCube(pos)) {
               makeBrokenPortal(world, pos.up());
               break;
            }
         }

      }
   }

   public static void makeBrokenPortal(World world, BlockPos pos) {
      Rotation rot = Rotation.values()[world.rand.nextInt(Rotation.values().length)];

      for(int i = 0; i < 4; ++i) {
         for(int j = 0; j < 5; ++j) {
            int[] xz = RotationHandler.applyRotation(rot, i, 0);
            BlockPos testPos = pos.add(xz[0], j, xz[1]);
            if (!world.isAirBlock(testPos)) {
               return;
            }
         }
      }

      IBlockState obsidian = Blocks.OBSIDIAN.getDefaultState();

      for(int i = 0; i < 4; ++i) {
         int[] xz = RotationHandler.applyRotation(rot, i, 0);
         BlockPos placePos = pos.add(xz[0], 0, xz[1]);
         world.setBlockState(placePos, obsidian);
      }

      int count = 2 + world.rand.nextInt(3);
      int placed = 4 + count;

      for(int i = 0; i < count; ++i) {
         BlockPos placePos = pos.add(0, i + 1, 0);
         world.setBlockState(placePos, obsidian);
      }

      count = 2 + world.rand.nextInt(3);
      placed = placed + count;
      int[] xz = RotationHandler.applyRotation(rot, 3, 0);

      for(int i = 0; i < count; ++i) {
         BlockPos placePos = pos.add(xz[0], i + 1, xz[1]);
         world.setBlockState(placePos, obsidian);
      }

      int missing = 13 - placed;
      int spread = 3;
      xz = RotationHandler.applyRotation(rot, 1, 0);

      label51:
      for(int i = 0; i < missing; ++i) {
         for(int tries = 0; tries < 20; ++tries) {
            BlockPos tryPos = pos.add(xz[0] + world.rand.nextInt(spread * 2) - spread, 3, xz[1] + world.rand.nextInt(spread * 2) - spread);
            if (world.isAirBlock(tryPos)) {
               while(true) {
                  if (!world.isAirBlock(tryPos)) {
                     world.setBlockState(tryPos.up(), obsidian);
                     continue label51;
                  }

                  tryPos = tryPos.down();
                  if (tryPos.getY() < 2) {
                     break;
                  }
               }
            }
         }
      }

   }
}
