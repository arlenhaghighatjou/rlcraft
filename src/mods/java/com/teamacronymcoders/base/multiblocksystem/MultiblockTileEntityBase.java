package com.teamacronymcoders.base.multiblocksystem;

import com.teamacronymcoders.base.Base;
import com.teamacronymcoders.base.tileentities.TileEntityBase;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public abstract class MultiblockTileEntityBase<T extends MultiblockControllerBase> extends TileEntityBase implements IMultiblockPart {
   private MultiblockControllerBase controller = null;
   private boolean visited = false;
   private boolean saveMultiblockData = false;
   private NBTTagCompound cachedMultiblockData = null;
   private static final IMultiblockRegistry REGISTRY = Base.proxy.initMultiblockRegistry();

   public MultiblockTileEntityBase() {
   }

   public Set<MultiblockControllerBase> attachToNeighbors() {
      Set<MultiblockControllerBase> controllers = null;
      MultiblockControllerBase bestController = null;
      IMultiblockPart[] partsToCheck = this.getNeighboringParts();

      for(IMultiblockPart neighborPart : partsToCheck) {
         if (neighborPart.isConnected()) {
            MultiblockControllerBase candidate = neighborPart.getMultiblockController();
            if (candidate.getClass().equals(this.getMultiblockControllerType())) {
               if (controllers == null) {
                  controllers = new HashSet();
                  bestController = candidate;
               } else if (!controllers.contains(candidate) && candidate.shouldConsume(bestController)) {
                  bestController = candidate;
               }

               controllers.add(candidate);
            }
         }
      }

      if (bestController != null) {
         this.controller = bestController;
         bestController.attachBlock(this);
      }

      return controllers;
   }

   public void assertDetached() {
      if (this.controller != null) {
         BlockPos coord = this.getWorldPosition();
         Base.instance.getLogger().info(String.format("[assert] Part @ (%d, %d, %d) should be detached already, but detected that it was not. This is not a fatal error, and will be repaired, but is unusual.", coord.getX(), coord.getY(), coord.getZ()));
         this.controller = null;
      }

   }

   protected void readFromDisk(NBTTagCompound data) {
      if (data.hasKey("multiblockData")) {
         this.cachedMultiblockData = data.getCompoundTag("multiblockData");
      }

   }

   protected NBTTagCompound writeToDisk(NBTTagCompound data) {
      if (this.isMultiblockSaveDelegate() && this.isConnected()) {
         NBTTagCompound multiblockData = new NBTTagCompound();
         this.getMultiblockController().writeToDisk(multiblockData);
         data.setTag("multiblockData", multiblockData);
      }

      return data;
   }

   public void invalidate() {
      super.invalidate();
      this.detachSelf(false);
   }

   public void onChunkUnload() {
      super.onChunkUnload();
      this.detachSelf(true);
   }

   public void validate() {
      super.validate();
      REGISTRY.onPartAdded(this.getWorld(), this);
   }

   public boolean hasMultiblockSaveData() {
      return this.cachedMultiblockData != null;
   }

   public NBTTagCompound getMultiblockSaveData() {
      return this.cachedMultiblockData;
   }

   public void onMultiblockDataAssimilated() {
      this.cachedMultiblockData = null;
   }

   public abstract void onMachineAssembled(MultiblockControllerBase var1);

   public abstract void onMachineBroken();

   public abstract void onMachineActivated();

   public abstract void onMachineDeactivated();

   public boolean isConnected() {
      return this.controller != null;
   }

   public T getMultiblockController() {
      return (T)this.controller;
   }

   public void becomeMultiblockSaveDelegate() {
      this.saveMultiblockData = true;
   }

   public void forfeitMultiblockSaveDelegate() {
      this.saveMultiblockData = false;
   }

   public boolean isMultiblockSaveDelegate() {
      return this.saveMultiblockData;
   }

   public void setUnvisited() {
      this.visited = false;
   }

   public void setVisited() {
      this.visited = true;
   }

   public boolean isVisited() {
      return this.visited;
   }

   public void onAssimilated(MultiblockControllerBase newController) {
      assert this.controller != newController;

      this.controller = newController;
   }

   public void onAttached(MultiblockControllerBase newController) {
      this.controller = newController;
   }

   public void onDetached(MultiblockControllerBase oldController) {
      this.controller = null;
   }

   public abstract MultiblockControllerBase createNewMultiblock();

   public IMultiblockPart[] getNeighboringParts() {
      List<IMultiblockPart> neighborParts = new ArrayList();
      BlockPos partPosition = this.getWorldPosition();

      for(EnumFacing facing : EnumFacing.VALUES) {
         BlockPos neighborPosition = partPosition.offset(facing);
         TileEntity te = this.getWorld().getTileEntity(neighborPosition);
         if (te instanceof IMultiblockPart) {
            neighborParts.add((IMultiblockPart)te);
         }
      }

      return (IMultiblockPart[])neighborParts.toArray(new IMultiblockPart[neighborParts.size()]);
   }

   public void onOrphaned(MultiblockControllerBase controller, int oldSize, int newSize) {
      this.markDirty();
      this.getWorld().markChunkDirty(this.getWorldPosition(), this);
   }

   public BlockPos getWorldPosition() {
      return this.pos;
   }

   public boolean isPartInvalid() {
      return this.isInvalid();
   }

   protected void notifyNeighborsOfBlockChange() {
      this.getWorld().notifyNeighborsOfStateChange(this.getWorldPosition(), this.getBlockType(), true);
   }

   protected void detachSelf(boolean chunkUnloading) {
      if (this.controller != null) {
         this.controller.detachBlock(this, chunkUnloading);
         this.controller = null;
      }

      REGISTRY.onPartRemovedFromWorld(this.getWorld(), this);
   }

   protected void markMultiblockForRenderUpdate() {
      MultiblockControllerBase controller = this.getMultiblockController();
      if (null != controller) {
         controller.markMultiblockForRenderUpdate();
      }

   }
}
