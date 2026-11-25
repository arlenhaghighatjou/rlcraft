package reborncore.common.explosion;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.apache.commons.lang3.time.StopWatch;
import reborncore.RebornCore;

public class RebornExplosion extends Explosion {
   @Nonnull
   BlockPos center;
   @Nonnull
   World world;
   @Nonnull
   int radius;
   @Nullable
   EntityLivingBase livingBase;

   public RebornExplosion(@Nonnull BlockPos center, @Nonnull World world, @Nonnull int radius) {
      super(world, (Entity)null, (double)center.getX(), (double)center.getY(), (double)center.getZ(), (float)radius, false, true);
      this.center = center;
      this.world = world;
      this.radius = radius;
   }

   public void setLivingBase(@Nullable EntityLivingBase livingBase) {
      this.livingBase = livingBase;
   }

   @Nullable
   public EntityLivingBase getLivingBase() {
      return this.livingBase;
   }

   public void explode() {
      StopWatch watch = new StopWatch();
      watch.start();

      for(int tx = -this.radius; tx < this.radius + 1; ++tx) {
         for(int ty = -this.radius; ty < this.radius + 1; ++ty) {
            for(int tz = -this.radius; tz < this.radius + 1; ++tz) {
               if (Math.sqrt(Math.pow((double)tx, 2.0D) + Math.pow((double)ty, 2.0D) + Math.pow((double)tz, 2.0D)) <= (double)(this.radius - 2)) {
                  BlockPos pos = this.center.add(tx, ty, tz);
                  IBlockState state = this.world.getBlockState(pos);
                  Block block = state.getBlock();
                  if (block != Blocks.BEDROCK && block != Blocks.AIR) {
                     block.onBlockExploded(this.world, pos, this);
                     this.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                  }
               }
            }
         }
      }

      RebornCore.logHelper.info("The explosion took" + watch + " to explode");
   }

   public void doExplosionA() {
      this.explode();
   }

   public void doExplosionB(boolean spawnParticles) {
      this.explode();
   }

   @Nullable
   public EntityLivingBase getExplosivePlacedBy() {
      return this.livingBase;
   }

   public List<BlockPos> getAffectedBlockPositions() {
      List<BlockPos> poses = new ArrayList();

      for(int tx = -this.radius; tx < this.radius + 1; ++tx) {
         for(int ty = -this.radius; ty < this.radius + 1; ++ty) {
            for(int tz = -this.radius; tz < this.radius + 1; ++tz) {
               if (Math.sqrt(Math.pow((double)tx, 2.0D) + Math.pow((double)ty, 2.0D) + Math.pow((double)tz, 2.0D)) <= (double)(this.radius - 2)) {
                  BlockPos pos = this.center.add(tx, ty, tz);
                  IBlockState state = this.world.getBlockState(pos);
                  Block block = state.getBlock();
                  if (block != Blocks.BEDROCK && block != Blocks.AIR) {
                     poses.add(pos);
                  }
               }
            }
         }
      }

      return poses;
   }
}
