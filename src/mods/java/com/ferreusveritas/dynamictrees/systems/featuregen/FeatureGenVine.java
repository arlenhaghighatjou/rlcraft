package com.ferreusveritas.dynamictrees.systems.featuregen;

import com.ferreusveritas.dynamictrees.api.IPostGenFeature;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import com.ferreusveritas.dynamictrees.util.LazyValue;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class FeatureGenVine implements IPostGenFeature {
   protected final PropertyBool[] vineMap = new PropertyBool[]{null, null, BlockVine.NORTH, BlockVine.SOUTH, BlockVine.WEST, BlockVine.EAST};
   protected int qty = 4;
   protected int maxLength = 8;
   protected float verSpread = 60.0F;
   protected float rayDistance = 5.0F;
   protected LazyValue<Block> vineBlock = LazyValue.<Block>supplied(() -> {
      return Block.getBlockFromName("vine");
   });

   public FeatureGenVine() {
   }

   public FeatureGenVine setQuantity(int qty) {
      this.qty = qty;
      return this;
   }

   public FeatureGenVine setMaxLength(int length) {
      this.maxLength = length;
      return this;
   }

   public FeatureGenVine setVerSpread(float verSpread) {
      this.verSpread = verSpread;
      return this;
   }

   public FeatureGenVine setRayDistance(float rayDistance) {
      this.rayDistance = rayDistance;
      return this;
   }

   public FeatureGenVine setVineBlock(Block vineBlock) {
      this.vineBlock = () -> {
         return vineBlock;
      };
      return this;
   }

   public boolean postGeneration(World world, BlockPos rootPos, Species species, Biome biome, int radius, List<BlockPos> endPoints, SafeChunkBounds safeBounds, IBlockState initialDirtState) {
      if (safeBounds != SafeChunkBounds.ANY && !endPoints.isEmpty()) {
         for(int i = 0; i < this.qty; ++i) {
            BlockPos endPoint = (BlockPos)endPoints.get(world.rand.nextInt(endPoints.size()));
            this.addVine(world, species, rootPos, endPoint, safeBounds);
         }

         return true;
      } else {
         return false;
      }
   }

   protected void addVine(World world, Species species, BlockPos rootPos, BlockPos branchPos, SafeChunkBounds safeBounds) {
      RayTraceResult result = CoordUtils.branchRayTrace(world, species, rootPos, branchPos, 90.0F, this.verSpread, this.rayDistance, safeBounds);
      if (result != null) {
         BlockPos vinePos = result.getBlockPos().offset(result.sideHit);
         if (vinePos != BlockPos.ORIGIN && safeBounds.inBounds(vinePos, true)) {
            PropertyBool vineSide = this.vineMap[result.sideHit.getOpposite().getIndex()];
            if (vineSide != null) {
               IBlockState vineState = ((Block)this.vineBlock.get()).getDefaultState().withProperty(vineSide, Boolean.valueOf(true));
               int len = MathHelper.clamp(world.rand.nextInt(this.maxLength) + 3, 3, this.maxLength);
               MutableBlockPos mPos = new MutableBlockPos(vinePos);

               for(int i = 0; i < len && world.isAirBlock(mPos); ++i) {
                  world.setBlockState(mPos, vineState);
                  mPos.setY(mPos.getY() - 1);
               }
            }
         }
      }

   }
}
