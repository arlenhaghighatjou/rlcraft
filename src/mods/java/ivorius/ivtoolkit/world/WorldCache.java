package ivorius.ivtoolkit.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class WorldCache {
   public final World world;
   private StructureBoundingBox boundingBox;
   private IBlockState[] states;

   public WorldCache(World world, StructureBoundingBox boundingBox) {
      this.world = world;
      this.boundingBox = boundingBox;
      this.states = new IBlockState[boundingBox.getXSize() * boundingBox.getYSize() * boundingBox.getZSize()];
   }

   protected Integer getIndex(BlockPos pos) {
      return !this.boundingBox.isVecInside(pos) ? null : ((pos.getX() - this.boundingBox.minX) * this.boundingBox.getYSize() + (pos.getY() - this.boundingBox.minY)) * this.boundingBox.getZSize() + (pos.getZ() - this.boundingBox.minZ);
   }

   public IBlockState getBlockState(BlockPos pos) {
      Integer index = this.getIndex(pos);
      if (index == null) {
         return this.world.getBlockState(pos);
      } else {
         IBlockState state = this.states[index.intValue()];
         return state != null ? state : (this.states[index.intValue()] = this.world.getBlockState(pos));
      }
   }

   public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
      boolean b = this.world.setBlockState(pos, state, flags);
      if (b) {
         Integer index = this.getIndex(pos);
         if (index != null) {
            this.states[index.intValue()] = state;
         }
      }

      return b;
   }
}
