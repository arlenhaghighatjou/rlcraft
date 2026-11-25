package com.ferreusveritas.dynamictrees.systems.nodemappers;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.SimpleVoxmap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NodeInflator implements INodeInspector {
   private float radius;
   private BlockPos last;
   Species species;
   SimpleVoxmap leafMap;

   public NodeInflator(Species species, SimpleVoxmap leafMap) {
      this.species = species;
      this.leafMap = leafMap;
      this.last = BlockPos.ORIGIN;
   }

   public boolean run(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      BlockBranch branch = TreeHelper.getBranch(blockState);
      if (branch != null) {
         this.radius = this.species.getFamily().getPrimaryThickness();
      }

      return false;
   }

   public boolean returnRun(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      BlockBranch branch = TreeHelper.getBranch(blockState);
      if (branch != null) {
         float areaAccum = this.radius * this.radius;
         boolean isTwig = true;

         for(EnumFacing dir : EnumFacing.VALUES) {
            if (!dir.equals(fromDir)) {
               BlockPos dPos = pos.offset(dir);
               if (dPos.equals(this.last)) {
                  isTwig = false;
               } else {
                  IBlockState deltaBlockState = world.getBlockState(dPos);
                  ITreePart treepart = TreeHelper.getTreePart(deltaBlockState);
                  if (branch.isSameTree(treepart)) {
                     int branchRadius = treepart.getRadius(deltaBlockState);
                     areaAccum += (float)(branchRadius * branchRadius);
                  }
               }
            }
         }

         if (isTwig) {
            if (this.leafMap != null) {
               this.leafMap.setVoxel(pos, (byte)16);
               SimpleVoxmap leafCluster = this.species.getLeavesProperties().getCellKit().getLeafCluster();
               this.leafMap.blitMax(pos, leafCluster);
            }
         } else {
            this.radius = (float)Math.sqrt((double)areaAccum) + this.species.getTapering() * this.species.getWorldGenTaperingFactor();
            int maxRadius = this.species.maxBranchRadius();
            if (this.radius > (float)maxRadius) {
               this.radius = (float)maxRadius;
            }

            float secondaryThickness = this.species.getFamily().getSecondaryThickness();
            if (this.radius < secondaryThickness) {
               this.radius = secondaryThickness;
            }

            branch.setRadius(world, pos, (int)Math.floor((double)this.radius), (EnumFacing)null);
            if (this.leafMap != null) {
               this.leafMap.setVoxel(pos, (byte)32);
            }
         }

         this.last = pos;
      }

      return false;
   }
}
