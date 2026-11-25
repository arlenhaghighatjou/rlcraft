package pl.asie.foamfix.coremod.injections.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockInfoInject implements IFoamFixPatchedBlockInfo {
   private BlockPos blockPos;
   private IBlockAccess world;
   private IBlockState state;
   private final int[][][] s = new int[3][3][3];
   private final int[][][] b = new int[3][3][3];
   private final float[][][] ao = new float[3][3][3];

   public BlockInfoInject() {
   }

   public int[][][] getRawS() {
      return this.s;
   }

   public int[][][] getRawB() {
      return this.b;
   }

   public void updateAO() {
      for(int x = 0; x <= 2; ++x) {
         for(int y = 0; y <= 2; ++y) {
            for(int z = 0; z <= 2; ++z) {
               BlockPos pos = this.blockPos.add(x - 1, y - 1, z - 1);
               IBlockState state = this.world.getBlockState(pos);
               this.ao[x][y][z] = state.getAmbientOcclusionLightValue();
            }
         }
      }

   }
}
