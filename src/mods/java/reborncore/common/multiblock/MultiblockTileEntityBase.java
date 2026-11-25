package reborncore.common.multiblock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import reborncore.RebornCore;

public abstract class MultiblockTileEntityBase extends IMultiblockPart implements ITickable {
   private MultiblockControllerBase controller = null;
   private boolean visited = false;
   private boolean saveMultiblockData = false;
   private NBTTagCompound cachedMultiblockData = null;

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
         RebornCore.logHelper.info(String.format("[assert] Part @ (%d, %d, %d) should be detached already, but detected that it was not. This is not a fatal error, and will be repaired, but is unusual.", this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()));
         this.controller = null;
      }

   }

   public void readFromNBT(NBTTagCompound data) {
      super.readFromNBT(data);
      if (data.hasKey("multiblockData")) {
         this.cachedMultiblockData = data.getCompoundTag("multiblockData");
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound data) {
      super.writeToNBT(data);
      if (this.isMultiblockSaveDelegate() && this.isConnected()) {
         NBTTagCompound multiblockData = new NBTTagCompound();
         this.controller.writeToNBT(multiblockData);
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
      MultiblockRegistry.onPartAdded(this.getWorld(), this);
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound packetData = new NBTTagCompound();
      this.encodeDescriptionPacket(packetData);
      return new SPacketUpdateTileEntity(this.getPos(), 0, packetData);
   }

   public void onDataPacket(NetworkManager network, SPacketUpdateTileEntity packet) {
      this.decodeDescriptionPacket(packet.getNbtCompound());
   }

   protected void encodeDescriptionPacket(NBTTagCompound packetData) {
      if (this.isMultiblockSaveDelegate() && this.isConnected()) {
         NBTTagCompound tag = new NBTTagCompound();
         this.getMultiblockController().formatDescriptionPacket(tag);
         packetData.setTag("multiblockData", tag);
      }

   }

   protected void decodeDescriptionPacket(NBTTagCompound packetData) {
      if (packetData.hasKey("multiblockData")) {
         NBTTagCompound tag = packetData.getCompoundTag("multiblockData");
         if (this.isConnected()) {
            this.getMultiblockController().decodeDescriptionPacket(tag);
         } else {
            this.cachedMultiblockData = tag;
         }
      }

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

   public MultiblockControllerBase getMultiblockController() {
      return this.controller;
   }

   public BlockPos getWorldLocation() {
      return this.getPos();
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
      BlockPos partPosition = this.getWorldLocation();

      for(EnumFacing facing : EnumFacing.VALUES) {
         BlockPos neighborPosition = partPosition.offset(facing);
         TileEntity te = this.world.getTileEntity(neighborPosition);
         if (te instanceof IMultiblockPart) {
            neighborParts.add((IMultiblockPart)te);
         }
      }

      return (IMultiblockPart[])neighborParts.toArray(new IMultiblockPart[neighborParts.size()]);
   }

   public void onOrphaned(MultiblockControllerBase controller, int oldSize, int newSize) {
      this.markDirty();
      this.getWorld().markChunkDirty(this.getPos(), this);
   }

   protected void notifyNeighborsOfBlockChange() {
      this.world.notifyNeighborsOfStateChange(this.getPos(), this.getBlockType(), true);
   }

   protected void notifyNeighborsOfTileChange() {
      this.world.notifyNeighborsOfStateChange(this.getPos(), this.getBlockType(), true);
   }

   protected void detachSelf(boolean chunkUnloading) {
      if (this.controller != null) {
         this.controller.detachBlock(this, chunkUnloading);
         this.controller = null;
      }

      MultiblockRegistry.onPartRemovedFromWorld(this.getWorld(), this);
   }

   public IBlockState getBlockState() {
      return this.world.getBlockState(this.pos);
   }
}
