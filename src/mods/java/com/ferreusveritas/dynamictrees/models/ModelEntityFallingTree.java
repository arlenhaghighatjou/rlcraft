package com.ferreusveritas.dynamictrees.models;

import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.client.QuadManipulator;
import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.BranchDestructionData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEntityFallingTree {
   protected final List<ModelEntityFallingTree.TreeQuadData> quads;
   protected final int entityId;

   public ModelEntityFallingTree(EntityFallingTree entity) {
      this.quads = this.generateTreeQuads(entity, entity.getEntityWorld());
      this.entityId = entity.getEntityId();
   }

   public List<ModelEntityFallingTree.TreeQuadData> getQuadData() {
      return this.quads;
   }

   public int getEntityId() {
      return this.entityId;
   }

   public static int getBrightness(EntityFallingTree entity) {
      BranchDestructionData destructionData = entity.getDestroyData();
      World world = entity.getEntityWorld();
      return world.getBlockState(destructionData.cutPos).getPackedLightmapCoords(world, destructionData.cutPos);
   }

   public List<ModelEntityFallingTree.TreeQuadData> generateTreeQuads(EntityFallingTree entity, World world) {
      BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
      BranchDestructionData destructionData = entity.getDestroyData();
      Species species = destructionData.species;
      BlockPos cutPos = destructionData.cutPos;
      EnumFacing cutDir = destructionData.cutDir;
      ArrayList<ModelEntityFallingTree.TreeQuadData> treeQuads = new ArrayList();
      if (destructionData.getNumBranches() <= 0) {
         return treeQuads;
      } else {
         IExtendedBlockState exState = destructionData.getBranchBlockState(0);
         if (exState == null) {
            return treeQuads;
         } else {
            for(EnumFacing face : EnumFacing.VALUES) {
               exState = exState.withProperty(BlockBranch.CONNECTIONS[face.getIndex()], Integer.valueOf(face == cutDir.getOpposite() ? 8 : 0));
            }

            int radius = ((BlockBranch)exState.getBlock()).getRadius(exState);
            float offset = (float)(8 - Math.min(radius, 8)) / 16.0F;
            IBakedModel branchModel = dispatcher.getModelForState(exState.getClean());
            treeQuads.addAll(toTreeQuadData(QuadManipulator.getQuads(branchModel, exState, (new Vec3d(BlockPos.ORIGIN.offset(cutDir))).scale((double)offset), new EnumFacing[]{cutDir}), -1, exState.getClean()));

            for(int index = 0; index < destructionData.getNumBranches(); ++index) {
               Block previousBranch = exState.getBlock();
               exState = destructionData.getBranchBlockState(index);
               if (!previousBranch.equals(exState.getBlock())) {
                  branchModel = dispatcher.getModelForState(exState.getClean());
               }

               BlockPos relPos = destructionData.getBranchRelPos(index);
               treeQuads.addAll(toTreeQuadData(QuadManipulator.getQuads(branchModel, exState, new Vec3d(relPos)), -1, exState.getClean()));
            }

            HashMap<BlockPos, IBlockState> leavesClusters = species.getFamily().getFellingLeavesClusters(destructionData);
            if (leavesClusters != null) {
               for(Entry<BlockPos, IBlockState> leafLoc : leavesClusters.entrySet()) {
                  IBlockState leafState = (IBlockState)leafLoc.getValue();
                  if (leafState instanceof IExtendedBlockState) {
                     leafState = ((IExtendedBlockState)leafState).getClean();
                  }

                  treeQuads.addAll(toTreeQuadData(QuadManipulator.getQuads(dispatcher.getModelForState(leafState), (IBlockState)leafLoc.getValue(), new Vec3d((Vec3i)leafLoc.getKey())), species.getLeavesProperties().foliageColorMultiplier(leafState, world, cutPos), leafState));
               }
            } else {
               for(int index = 0; index < destructionData.getNumLeaves(); ++index) {
                  BlockPos relPos = destructionData.getLeavesRelPos(index);
                  IBlockState state = destructionData.getLeavesBlockState(index);
                  IBakedModel leavesModel = dispatcher.getModelForState(state);
                  treeQuads.addAll(toTreeQuadData(QuadManipulator.getQuads(leavesModel, state, new Vec3d(relPos)), destructionData.getLeavesProperties(index).foliageColorMultiplier(state, world, cutPos.add(relPos)), state));
               }
            }

            return treeQuads;
         }
      }
   }

   public static List<ModelEntityFallingTree.TreeQuadData> toTreeQuadData(List<BakedQuad> bakedQuads, int color, IBlockState state) {
      return (List)bakedQuads.stream().map((bakedQuad) -> {
         return new ModelEntityFallingTree.TreeQuadData(bakedQuad, color, state);
      }).collect(Collectors.toList());
   }

   public static final class TreeQuadData {
      public final BakedQuad bakedQuad;
      public final IBlockState state;
      public final int color;

      public TreeQuadData(BakedQuad bakedQuad, int color, IBlockState state) {
         this.bakedQuad = bakedQuad;
         this.state = state;
         this.color = color;
      }
   }
}
