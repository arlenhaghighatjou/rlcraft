package com.ferreusveritas.dynamictrees.util;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.google.common.collect.AbstractIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.property.IExtendedBlockState;

public class BranchDestructionData {
   public final Species species;
   public final int[] destroyedBranchesRadiusPosition;
   public final int[] destroyedBranchesConnections;
   public final int[] destroyedBranchesBlockIndex;
   public final int[] destroyedLeaves;
   public final int[] destroyedLeavesBlockIndex;
   public final List<BlockBranch.BlockItemStack> leavesDrops;
   public final int[] endPoints;
   public final float woodVolume;
   public final EnumFacing cutDir;
   public final EnumFacing toolDir;
   public final BlockPos cutPos;
   public final int trunkHeight;
   public static final BlockBounds bounds = new BlockBounds(new BlockPos(-64, -64, -64), new BlockPos(64, 64, 64));

   public BranchDestructionData() {
      this.species = Species.NULLSPECIES;
      this.destroyedBranchesConnections = new int[0];
      this.destroyedBranchesRadiusPosition = new int[0];
      this.destroyedBranchesBlockIndex = new int[0];
      this.destroyedLeaves = new int[0];
      this.destroyedLeavesBlockIndex = new int[0];
      this.leavesDrops = new ArrayList(0);
      this.endPoints = new int[0];
      this.woodVolume = 0.0F;
      this.cutDir = EnumFacing.DOWN;
      this.toolDir = EnumFacing.DOWN;
      this.cutPos = BlockPos.ORIGIN;
      this.trunkHeight = 0;
   }

   public BranchDestructionData(Species species, Map<BlockPos, IExtendedBlockState> branches, Map<BlockPos, IBlockState> leaves, List<BlockBranch.BlockItemStack> leavesDrops, List<BlockPos> ends, float volume, BlockPos cutPos, EnumFacing cutDir, EnumFacing toolDir, int trunkHeight) {
      this.species = species;
      int[][] encodedBranchData = this.convertBranchesToIntArrays(branches);
      this.destroyedBranchesRadiusPosition = encodedBranchData[0];
      this.destroyedBranchesConnections = encodedBranchData[1];
      this.destroyedBranchesBlockIndex = encodedBranchData[2];
      int[][] encodedLeavesData = this.convertLeavesToIntArray(leaves);
      this.destroyedLeaves = encodedLeavesData[0];
      this.destroyedLeavesBlockIndex = encodedLeavesData[1];
      this.leavesDrops = leavesDrops;
      this.endPoints = this.convertEndPointsToIntArray(ends);
      this.woodVolume = volume;
      this.cutPos = cutPos;
      this.cutDir = cutDir;
      this.toolDir = toolDir;
      this.trunkHeight = trunkHeight;
   }

   public BranchDestructionData(NBTTagCompound nbt) {
      this.species = TreeRegistry.findSpecies(new ResourceLocation(nbt.getString("species")));
      this.destroyedBranchesRadiusPosition = nbt.getIntArray("branchpos");
      this.destroyedBranchesConnections = nbt.getIntArray("branchcon");
      this.destroyedBranchesBlockIndex = nbt.getIntArray("branchblock");
      this.destroyedLeaves = nbt.getIntArray("leaves");
      this.destroyedLeavesBlockIndex = nbt.getIntArray("leavesblock");
      this.leavesDrops = new ArrayList();
      this.endPoints = nbt.getIntArray("ends");
      this.woodVolume = nbt.getFloat("volume");
      this.cutPos = new BlockPos(nbt.getInteger("cutx"), nbt.getInteger("cuty"), nbt.getInteger("cutz"));
      this.cutDir = EnumFacing.values()[net.minecraft.util.math.MathHelper.clamp(nbt.getInteger("cutdir"), 0, EnumFacing.values().length - 1)];
      this.toolDir = EnumFacing.values()[net.minecraft.util.math.MathHelper.clamp(nbt.getInteger("tooldir"), 0, EnumFacing.values().length - 1)];
      this.trunkHeight = nbt.getInteger("trunkheight");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound tag) {
      tag.setString("species", this.species.toString());
      tag.setIntArray("branchpos", this.destroyedBranchesRadiusPosition);
      tag.setIntArray("branchcon", this.destroyedBranchesConnections);
      tag.setIntArray("branchblock", this.destroyedBranchesBlockIndex);
      tag.setIntArray("leaves", this.destroyedLeaves);
      tag.setIntArray("leavesblock", this.destroyedLeavesBlockIndex);
      tag.setIntArray("ends", this.endPoints);
      tag.setFloat("volume", this.woodVolume);
      tag.setInteger("cutx", this.cutPos.getX());
      tag.setInteger("cuty", this.cutPos.getY());
      tag.setInteger("cutz", this.cutPos.getZ());
      tag.setInteger("cutdir", this.cutDir.getIndex());
      tag.setInteger("tooldir", this.toolDir.getIndex());
      tag.setInteger("trunkheight", this.trunkHeight);
      return tag;
   }

   private int[][] convertBranchesToIntArrays(Map<BlockPos, IExtendedBlockState> branchList) {
      int[] relPosData = new int[branchList.size()];
      int[] connectionData = new int[branchList.size()];
      int[] blockIndexData = new int[branchList.size()];
      int index = 0;
      IExtendedBlockState origExState = (IExtendedBlockState)branchList.get(BlockPos.ORIGIN);
      if (origExState != null) {
         relPosData[index] = this.encodeBranchesRadiusPos(BlockPos.ORIGIN, (BlockBranch)origExState.getBlock(), origExState);
         connectionData[index] = this.encodeBranchesConnections(origExState);
         blockIndexData[index++] = this.encodeBranchBlocks((BlockBranch)origExState.getBlock());
         branchList.remove(BlockPos.ORIGIN);
      }

      for(Entry<BlockPos, IExtendedBlockState> set : branchList.entrySet()) {
         BlockPos relPos = (BlockPos)set.getKey();
         IExtendedBlockState exState = (IExtendedBlockState)set.getValue();
         Block block = exState.getBlock();
         if (block instanceof BlockBranch && bounds.inBounds(relPos)) {
            relPosData[index] = this.encodeBranchesRadiusPos(relPos, (BlockBranch)block, exState);
            connectionData[index] = this.encodeBranchesConnections(exState);
            blockIndexData[index++] = this.encodeBranchBlocks((BlockBranch)block);
         }
      }

      relPosData = Arrays.copyOf(relPosData, index);
      connectionData = Arrays.copyOf(connectionData, index);
      blockIndexData = Arrays.copyOf(blockIndexData, index);
      return new int[][]{relPosData, connectionData, blockIndexData};
   }

   private int encodeBranchesRadiusPos(BlockPos relPos, BlockBranch branchBlock, IBlockState state) {
      return (branchBlock.getRadius(state) & 31) << 24 | this.encodeRelBlockPos(relPos);
   }

   private int encodeBranchesConnections(IExtendedBlockState exState) {
      int result = 0;

      for(EnumFacing face : EnumFacing.values()) {
         int rad = ((Integer)exState.getValue(BlockBranch.CONNECTIONS[face.getIndex()])).intValue();
         result |= (rad & 31) << face.getIndex() * 5;
      }

      return result;
   }

   private int encodeBranchBlocks(BlockBranch branch) {
      return branch.getFamily().getBranchBlockIndex(branch);
   }

   public int getNumBranches() {
      return this.destroyedBranchesRadiusPosition.length;
   }

   public BlockPos getBranchRelPos(int index) {
      return this.decodeRelPos(this.destroyedBranchesRadiusPosition[index]);
   }

   public int getBranchRadius(int index) {
      return this.decodeBranchRadius(this.destroyedBranchesRadiusPosition[index]);
   }

   private int decodeBranchRadius(int encoded) {
      return encoded >> 24 & 31;
   }

   public IExtendedBlockState getBranchBlockState(int index) {
      return this.decodeBranchBlockState(this.destroyedBranchesRadiusPosition[index], this.destroyedBranchesConnections[index], this.destroyedBranchesBlockIndex[index]);
   }

   private IExtendedBlockState decodeBranchBlockState(int encodedRadPos, int encodedConnections, int encodedBlockIndex) {
      BlockBranch branch = this.species.getFamily().getValidBranchBlock(encodedBlockIndex);
      if (branch != null) {
         int radius = this.decodeBranchRadius(encodedRadPos);
         IBlockState state = branch.getStateForRadius(radius);
         if (state instanceof IExtendedBlockState) {
            IExtendedBlockState exState = (IExtendedBlockState)state;

            for(EnumFacing face : EnumFacing.values()) {
               int rad = encodedConnections >> face.getIndex() * 5 & 31;
               exState = exState.withProperty(BlockBranch.CONNECTIONS[face.getIndex()], Integer.valueOf(net.minecraft.util.math.MathHelper.clamp(rad, 0, 8)));
            }

            return exState;
         }
      }

      return null;
   }

   private int[][] convertLeavesToIntArray(Map<BlockPos, IBlockState> leavesList) {
      int[] posData = new int[leavesList.size()];
      int[] blockIndexData = new int[leavesList.size()];
      int index = 0;

      for(Entry<BlockPos, IBlockState> set : leavesList.entrySet()) {
         BlockPos relPos = (BlockPos)set.getKey();
         IBlockState state = (IBlockState)set.getValue();
         Block block = state.getBlock();
         if (block instanceof BlockDynamicLeaves && bounds.inBounds(relPos)) {
            posData[index] = this.encodeLeaves(relPos, (BlockDynamicLeaves)block, state);
            blockIndexData[index++] = this.encodeLeavesBlocks(state, this.species);
         }
      }

      posData = Arrays.copyOf(posData, index);
      blockIndexData = Arrays.copyOf(blockIndexData, index);
      return new int[][]{posData, blockIndexData};
   }

   private int encodeLeaves(BlockPos relPos, BlockDynamicLeaves block, IBlockState state) {
      return ((Integer)state.getValue(BlockDynamicLeaves.HYDRO)).intValue() << 24 | this.encodeRelBlockPos(relPos);
   }

   private int encodeLeavesBlocks(IBlockState state, Species species) {
      return species.getLeavesBlockIndex(state);
   }

   public int getNumLeaves() {
      return this.destroyedLeaves.length;
   }

   public BlockPos getLeavesRelPos(int index) {
      return this.decodeLeavesRelPos(this.destroyedLeaves[index]);
   }

   private BlockPos decodeLeavesRelPos(int encoded) {
      return this.decodeRelPos(encoded);
   }

   public int getLeavesHydro(int index) {
      return this.decodeLeavesHydro(this.destroyedLeaves[index]);
   }

   private int decodeLeavesHydro(int encoded) {
      return encoded >> 24 & 15;
   }

   public ILeavesProperties getLeavesProperties(int index) {
      return this.species.getValidLeavesProperties(this.destroyedLeavesBlockIndex[index]);
   }

   public IBlockState getLeavesBlockState(int index) {
      return this.species.getValidLeavesBlock(this.destroyedLeavesBlockIndex[index]);
   }

   private int[] convertEndPointsToIntArray(List<BlockPos> endPoints) {
      int[] data = new int[endPoints.size()];
      int index = 0;

      for(BlockPos relPos : endPoints) {
         if (bounds.inBounds(relPos)) {
            data[index++] = this.encodeRelBlockPos(relPos);
         }
      }

      return Arrays.copyOf(data, index);
   }

   public int getNumEndpoints() {
      return this.endPoints.length;
   }

   public BlockPos getEndPointRelPos(int index) {
      return this.decodeRelPos(this.endPoints[index]);
   }

   public Iterable<BlockPos> getPositions(BranchDestructionData.PosType posType) {
      return this.getPositions(posType, true);
   }

   public Iterable<BlockPos> getPositions(BranchDestructionData.PosType posType, boolean absolute) {
      final Function<Integer, BlockPos> getter;
      final int limit;
      switch(posType) {
      case BRANCHES:
      default:
         getter = absolute ? (i) -> {
            return this.getBranchRelPos(i.intValue()).add(this.cutPos);
         } : (i) -> {
            return this.getBranchRelPos(i.intValue());
         };
         limit = this.getNumBranches();
         break;
      case ENDPOINTS:
         getter = absolute ? (i) -> {
            return this.getEndPointRelPos(i.intValue()).add(this.cutPos);
         } : (i) -> {
            return this.getEndPointRelPos(i.intValue());
         };
         limit = this.getNumEndpoints();
         break;
      case LEAVES:
         getter = absolute ? (i) -> {
            return this.getLeavesRelPos(i.intValue()).add(this.cutPos);
         } : (i) -> {
            return this.getLeavesRelPos(i.intValue());
         };
         limit = this.getNumLeaves();
      }

      return new Iterable<BlockPos>() {
         public Iterator<BlockPos> iterator() {
            return new AbstractIterator<BlockPos>() {
               private int index = 0;

               protected BlockPos computeNext() {
                  return this.index < limit ? (BlockPos)getter.apply(Integer.valueOf(this.index++)) : (BlockPos)this.endOfData();
               }
            };
         }
      };
   }

   private int encodeRelBlockPos(BlockPos relPos) {
      return (relPos.getX() + 64 & 255) << 16 | (relPos.getY() + 64 & 255) << 8 | (relPos.getZ() + 64 & 255) << 0;
   }

   private BlockPos decodeRelPos(int encoded) {
      return new BlockPos((encoded >> 16 & 255) - 64, (encoded >> 8 & 255) - 64, (encoded >> 0 & 255) - 64);
   }

   public static enum PosType {
      BRANCHES,
      LEAVES,
      ENDPOINTS;

      private PosType() {
      }
   }
}
