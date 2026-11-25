package com.ferreusveritas.dynamictrees.systems.featuregen;

import com.ferreusveritas.dynamictrees.api.IFullGenFeature;
import com.ferreusveritas.dynamictrees.api.IPostGenFeature;
import com.ferreusveritas.dynamictrees.cells.LeafClusters;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.ferreusveritas.dynamictrees.util.SimpleVoxmap;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class FeatureGenBush implements IFullGenFeature, IPostGenFeature {
   private IBlockState logState = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, EnumType.OAK);
   private IBlockState leavesState = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, EnumType.OAK).withProperty(BlockOldLeaf.CHECK_DECAY, Boolean.valueOf(false));
   private IBlockState secondaryLeavesState = null;
   private Predicate<Biome> biomePredicate = (i) -> {
      return true;
   };

   public FeatureGenBush() {
   }

   public FeatureGenBush setLogState(IBlockState logState) {
      this.logState = logState;
      return this;
   }

   public FeatureGenBush setLeavesState(IBlockState leavesState) {
      this.leavesState = leavesState;
      return this;
   }

   public FeatureGenBush setSecondaryLeavesState(IBlockState secondaryLeavesState) {
      this.secondaryLeavesState = secondaryLeavesState;
      return this;
   }

   public FeatureGenBush setBiomePredicate(Predicate<Biome> biomePredicate) {
      this.biomePredicate = biomePredicate;
      return this;
   }

   public boolean generate(World world, BlockPos rootPos, Species species, Biome biome, Random random, int radius, SafeChunkBounds safeBounds) {
      this.commonGen(world, rootPos, species, random, radius, safeBounds);
      return true;
   }

   public boolean postGeneration(World world, BlockPos rootPos, Species species, Biome biome, int radius, List<BlockPos> endPoints, SafeChunkBounds safeBounds, IBlockState initialDirtState) {
      if (safeBounds != SafeChunkBounds.ANY && this.biomePredicate.test(biome)) {
         this.commonGen(world, rootPos, species, world.rand, radius, safeBounds);
         return true;
      } else {
         return false;
      }
   }

   protected void commonGen(World world, BlockPos rootPos, Species species, Random random, int radius, SafeChunkBounds safeBounds) {
      if (radius > 2) {
         Vec3d vTree = (new Vec3d(rootPos)).addVector(0.5D, 0.5D, 0.5D);

         for(int i = 0; i < 2; ++i) {
            int rad = MathHelper.clamp(world.rand.nextInt(radius - 2) + 2, 2, radius - 1);
            Vec3d v = vTree.add((new Vec3d(1.0D, 0.0D, 0.0D)).scale((double)rad).rotateYaw((float)((double)random.nextFloat() * 3.141592653589793D * 2.0D)));
            BlockPos vPos = new BlockPos(v);
            if (safeBounds.inBounds(vPos, true)) {
               BlockPos pos = CoordUtils.findGround(world, vPos);
               IBlockState soilBlockState = world.getBlockState(pos);
               pos = pos.up();
               if (!world.getBlockState(pos).getMaterial().isLiquid() && species.isAcceptableSoil(world, pos, soilBlockState)) {
                  world.setBlockState(pos, this.logState);
                  SimpleVoxmap leafMap = LeafClusters.bush;
                  MutableBlockPos leafPos = new MutableBlockPos();

                  for(MutableBlockPos dPos : leafMap.getAllNonZero()) {
                     leafPos.setPos(pos.getX() + dPos.getX(), pos.getY() + dPos.getY(), pos.getZ() + dPos.getZ());
                     if (safeBounds.inBounds(leafPos, true) && coordHashCode(leafPos) % 5 != 0 && world.getBlockState(leafPos).getBlock().isReplaceable(world, leafPos)) {
                        world.setBlockState(leafPos, this.secondaryLeavesState != null && random.nextInt(4) == 0 ? this.secondaryLeavesState : this.leavesState);
                     }
                  }
               }
            }
         }

      }
   }

   public static int coordHashCode(BlockPos pos) {
      int hash = (pos.getX() * 4111 ^ pos.getY() * 271 ^ pos.getZ() * 3067) >> 1;
      return hash & '\uffff';
   }
}
