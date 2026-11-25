package vazkii.quark.world.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import vazkii.arl.block.tile.TileMod;

public class TileSmoker extends TileMod implements ITickable {
   public TileSmoker() {
   }

   public void update() {
      if (this.world.isRemote && !this.isFullBlock(this.pos.up())) {
         BlockPos source = this.findChimneyEnd();
         int yDiff = source.getY() - this.pos.getY();

         for(int i = 0; i < yDiff - 1; ++i) {
            float x = (float)this.pos.getX() + 0.2F + this.world.rand.nextFloat() * 0.6F;
            float y = (float)this.pos.getY() + this.world.rand.nextFloat() * (float)(yDiff - 1) - 0.4F;
            float z = (float)this.pos.getZ() + 0.2F + this.world.rand.nextFloat() * 0.6F;
            this.world.spawnAlwaysVisibleParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(), (double)x, (double)y, (double)z, 0.0D, 0.0D, 0.0D, new int[0]);
         }

         for(int i = 0; i < 6; ++i) {
            float x = (float)source.getX() + 0.2F + this.world.rand.nextFloat() * 0.6F;
            float y = (float)source.getY() + this.world.rand.nextFloat() - 0.4F;
            float z = (float)source.getZ() + 0.2F + this.world.rand.nextFloat() * 0.6F;
            this.world.spawnAlwaysVisibleParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(), (double)x, (double)y, (double)z, 0.0D, 0.0D, 0.0D, new int[0]);
         }

      }
   }

   private boolean isFullBlock(BlockPos pos) {
      IBlockState state = this.world.getBlockState(pos);
      AxisAlignedBB aabb = state.getCollisionBoundingBox(this.world, pos);
      return Block.FULL_BLOCK_AABB.equals(aabb);
   }

   private BlockPos findChimneyEnd() {
      BlockPos nextPos;
      for(BlockPos currPos = this.pos; currPos.getY() < 255; currPos = nextPos) {
         nextPos = currPos.up();
         if (this.isFullBlock(nextPos)) {
            return currPos;
         }

         for(EnumFacing face : EnumFacing.HORIZONTALS) {
            BlockPos checkPos = nextPos.offset(face);
            if (!this.isFullBlock(checkPos)) {
               return nextPos;
            }
         }
      }

      return this.pos;
   }
}
