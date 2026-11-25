package com.teamacronymcoders.base.multiblocksystem;

import com.teamacronymcoders.base.Base;
import com.teamacronymcoders.base.multiblocksystem.validation.IMultiblockValidator;
import com.teamacronymcoders.base.multiblocksystem.validation.ValidationError;
import com.teamacronymcoders.base.util.Utils;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public abstract class MultiblockControllerBase implements IMultiblockValidator {
   public static final short DIMENSION_UNBOUNDED = -1;
   public final World WORLD;
   protected MultiblockControllerBase.AssemblyState assemblyState;
   protected HashSet<IMultiblockPart> connectedParts;
   private BlockPos referenceCoord;
   private BlockPos minimumCoord;
   private BlockPos maximumCoord;
   private boolean shouldCheckForDisconnections;
   private ValidationError lastValidationError;
   protected boolean debugMode;
   private static final IMultiblockRegistry REGISTRY = Base.proxy.initMultiblockRegistry();

   protected MultiblockControllerBase(World world) {
      this.WORLD = world;
      this.connectedParts = new HashSet();
      this.referenceCoord = null;
      this.assemblyState = MultiblockControllerBase.AssemblyState.Disassembled;
      this.minimumCoord = null;
      this.maximumCoord = null;
      this.shouldCheckForDisconnections = true;
      this.lastValidationError = null;
      this.debugMode = false;
   }

   public void setDebugMode(boolean active) {
      this.debugMode = active;
   }

   public boolean isDebugMode() {
      return this.debugMode;
   }

   public abstract void onAttachedPartWithMultiblockData(IMultiblockPart var1, NBTTagCompound var2);

   public boolean hasBlock(BlockPos blockCoord) {
      return this.WORLD.getTileEntity(blockCoord) instanceof IMultiblockPart ? this.connectedParts.contains((IMultiblockPart)this.WORLD.getTileEntity(blockCoord)) : false;
   }

   public void attachBlock(IMultiblockPart part) {
      BlockPos coord = part.getWorldPosition();
      if (!this.connectedParts.add(part)) {
         Base.instance.getLogger().warning(String.format("[%s] Controller %s is double-adding part %d @ %s. This is unusual. If you encounter odd behavior, please tear down the machine and rebuild it.", this.WORLD.isRemote ? "CLIENT" : "SERVER", this.hashCode(), part.hashCode(), coord));
      }

      part.onAttached(this);
      this.onBlockAdded(part);
      if (part.hasMultiblockSaveData()) {
         NBTTagCompound savedData = part.getMultiblockSaveData();
         this.onAttachedPartWithMultiblockData(part, savedData);
         part.onMultiblockDataAssimilated();
      }

      if (this.referenceCoord == null) {
         this.referenceCoord = coord;
         part.becomeMultiblockSaveDelegate();
      } else if (coord.compareTo(this.referenceCoord) < 0) {
         TileEntity te = this.WORLD.getTileEntity(this.referenceCoord);
         ((IMultiblockPart)te).forfeitMultiblockSaveDelegate();
         this.referenceCoord = coord;
         part.becomeMultiblockSaveDelegate();
      } else {
         part.forfeitMultiblockSaveDelegate();
      }

      BlockPos partPos = part.getWorldPosition();
      if (this.minimumCoord != null) {
         int curX = this.minimumCoord.getX();
         int curY = this.minimumCoord.getY();
         int curZ = this.minimumCoord.getZ();
         int partCoord = partPos.getX();
         int newX = partCoord < curX ? partCoord : curX;
         partCoord = partPos.getY();
         int newY = partCoord < curY ? partCoord : curY;
         partCoord = partPos.getZ();
         int newZ = partCoord < curZ ? partCoord : curZ;
         if (newX != curX || newY != curY || newZ != curZ) {
            this.minimumCoord = new BlockPos(newX, newY, newZ);
         }
      }

      if (this.maximumCoord != null) {
         int curX = this.maximumCoord.getX();
         int curY = this.maximumCoord.getY();
         int curZ = this.maximumCoord.getZ();
         int partCoord = partPos.getX();
         int newX = partCoord > curX ? partCoord : curX;
         partCoord = partPos.getY();
         int newY = partCoord > curY ? partCoord : curY;
         partCoord = partPos.getZ();
         int newZ = partCoord > curZ ? partCoord : curZ;
         if (newX != curX || newY != curY || newZ != curZ) {
            this.maximumCoord = new BlockPos(newX, newY, newZ);
         }
      }

      REGISTRY.addDirtyController(this.WORLD, this);
   }

   protected abstract void onBlockAdded(IMultiblockPart var1);

   protected abstract void onBlockRemoved(IMultiblockPart var1);

   protected abstract void onMachineAssembled();

   protected abstract void onMachineRestored();

   protected abstract void onMachinePaused();

   protected abstract void onMachineDisassembled();

   private void onDetachBlock(IMultiblockPart part) {
      part.onDetached(this);
      this.onBlockRemoved(part);
      part.forfeitMultiblockSaveDelegate();
      this.minimumCoord = this.maximumCoord = null;
      if (this.referenceCoord != null && this.referenceCoord.equals(part.getWorldPosition())) {
         this.referenceCoord = null;
      }

      this.shouldCheckForDisconnections = true;
   }

   public void detachBlock(IMultiblockPart part, boolean chunkUnloading) {
      if (chunkUnloading && this.assemblyState == MultiblockControllerBase.AssemblyState.Assembled) {
         this.assemblyState = MultiblockControllerBase.AssemblyState.Paused;
         this.onMachinePaused();
      }

      this.onDetachBlock(part);
      if (!this.connectedParts.remove(part)) {
         BlockPos position = part.getWorldPosition();
         Base.instance.getLogger().warning(String.format("[%s] Double-removing part (%d) @ %d, %d, %d, this is unexpected and may cause problems. If you encounter anomalies, please tear down the reactor and rebuild it.", this.WORLD.isRemote ? "CLIENT" : "SERVER", part.hashCode(), position.getX(), position.getY(), position.getZ()));
      }

      if (this.connectedParts.isEmpty()) {
         REGISTRY.addDeadController(this.WORLD, this);
      } else {
         REGISTRY.addDirtyController(this.WORLD, this);
         if (this.referenceCoord == null) {
            this.selectNewReferenceCoord();
         }

      }
   }

   protected abstract int getMinimumNumberOfBlocksForAssembledMachine();

   public abstract int getMaximumXSize();

   public abstract int getMaximumZSize();

   public abstract int getMaximumYSize();

   public int getMinimumXSize() {
      return 1;
   }

   public int getMinimumYSize() {
      return 1;
   }

   public int getMinimumZSize() {
      return 1;
   }

   public ValidationError getLastError() {
      return this.lastValidationError;
   }

   public void setLastError(ValidationError error) {
      if (null == error) {
         throw new IllegalArgumentException("The validation error can't be null");
      } else {
         this.lastValidationError = error;
      }
   }

   public void setLastError(String messageFormatStringResourceKey, Object... messageParameters) {
      this.lastValidationError = new ValidationError(messageFormatStringResourceKey, messageParameters);
   }

   protected abstract boolean isMachineWhole(IMultiblockValidator var1);

   public void checkIfMachineIsWhole() {
      MultiblockControllerBase.AssemblyState oldState = this.assemblyState;
      this.lastValidationError = null;
      if (this.isMachineWhole(this)) {
         this.assembleMachine(oldState);
      } else if (oldState == MultiblockControllerBase.AssemblyState.Assembled) {
         this.disassembleMachine();
      }

   }

   private void assembleMachine(MultiblockControllerBase.AssemblyState oldState) {
      for(IMultiblockPart part : this.connectedParts) {
         part.onMachineAssembled(this);
      }

      this.assemblyState = MultiblockControllerBase.AssemblyState.Assembled;
      if (oldState == MultiblockControllerBase.AssemblyState.Paused) {
         this.onMachineRestored();
      } else {
         this.onMachineAssembled();
      }

   }

   private void disassembleMachine() {
      for(IMultiblockPart part : this.connectedParts) {
         part.onMachineBroken();
      }

      this.assemblyState = MultiblockControllerBase.AssemblyState.Disassembled;
      this.onMachineDisassembled();
   }

   public void assimilate(MultiblockControllerBase other) {
      BlockPos otherReferenceCoord = other.getReferenceCoord();
      if (otherReferenceCoord != null && this.getReferenceCoord().compareTo(otherReferenceCoord) >= 0) {
         throw new IllegalArgumentException("The controller with the lowest minimum-coord value must consume the one with the higher coords");
      } else {
         Set<IMultiblockPart> partsToAcquire = new HashSet(other.connectedParts);
         other._onAssimilated(this);

         for(IMultiblockPart acquiredPart : partsToAcquire) {
            if (!acquiredPart.isPartInvalid()) {
               this.connectedParts.add(acquiredPart);
               acquiredPart.onAssimilated(this);
               this.onBlockAdded(acquiredPart);
            }
         }

         this.onAssimilate(other);
         other.onAssimilated(this);
      }
   }

   private void _onAssimilated(MultiblockControllerBase otherController) {
      if (this.referenceCoord != null) {
         if (this.WORLD.isBlockLoaded(this.referenceCoord)) {
            TileEntity te = this.WORLD.getTileEntity(this.referenceCoord);
            if (te instanceof IMultiblockPart) {
               ((IMultiblockPart)te).forfeitMultiblockSaveDelegate();
            }
         }

         this.referenceCoord = null;
      }

      this.connectedParts.clear();
   }

   protected abstract void onAssimilate(MultiblockControllerBase var1);

   protected abstract void onAssimilated(MultiblockControllerBase var1);

   public final void updateMultiblockEntity() {
      if (this.connectedParts.isEmpty()) {
         REGISTRY.addDeadController(this.WORLD, this);
      } else if (this.assemblyState == MultiblockControllerBase.AssemblyState.Assembled) {
         if (this.WORLD.isRemote) {
            this.updateClient();
         } else if (this.updateServer() && this.minimumCoord != null && this.maximumCoord != null && this.WORLD.isAreaLoaded(this.minimumCoord, this.maximumCoord)) {
            int minChunkX = Utils.getChunkXFromBlock(this.minimumCoord);
            int minChunkZ = Utils.getChunkZFromBlock(this.minimumCoord);
            int maxChunkX = Utils.getChunkXFromBlock(this.maximumCoord);
            int maxChunkZ = Utils.getChunkZFromBlock(this.maximumCoord);

            for(int x = minChunkX; x <= maxChunkX; ++x) {
               for(int z = minChunkZ; z <= maxChunkZ; ++z) {
                  Chunk chunkToSave = this.WORLD.getChunkFromChunkCoords(x, z);
                  chunkToSave.setModified(true);
               }
            }
         }

      }
   }

   protected abstract boolean updateServer();

   protected abstract void updateClient();

   protected abstract boolean isBlockGoodForFrame(World var1, int var2, int var3, int var4, IMultiblockValidator var5);

   protected abstract boolean isBlockGoodForTop(World var1, int var2, int var3, int var4, IMultiblockValidator var5);

   protected abstract boolean isBlockGoodForBottom(World var1, int var2, int var3, int var4, IMultiblockValidator var5);

   protected abstract boolean isBlockGoodForSides(World var1, int var2, int var3, int var4, IMultiblockValidator var5);

   protected abstract boolean isBlockGoodForInterior(World var1, int var2, int var3, int var4, IMultiblockValidator var5);

   public BlockPos getReferenceCoord() {
      if (this.referenceCoord == null) {
         this.selectNewReferenceCoord();
      }

      return this.referenceCoord;
   }

   public int getNumConnectedBlocks() {
      return this.connectedParts.size();
   }

   public void recalculateMinMaxCoords() {
      int minZ = Integer.MAX_VALUE;
      int minY = Integer.MAX_VALUE;
      int minX = Integer.MAX_VALUE;
      int maxZ = Integer.MIN_VALUE;
      int maxY = Integer.MIN_VALUE;
      int maxX = Integer.MIN_VALUE;

      for(IMultiblockPart part : this.connectedParts) {
         BlockPos partPos = part.getWorldPosition();
         int partCoord = partPos.getX();
         if (partCoord < minX) {
            minX = partCoord;
         }

         if (partCoord > maxX) {
            maxX = partCoord;
         }

         partCoord = partPos.getY();
         if (partCoord < minY) {
            minY = partCoord;
         }

         if (partCoord > maxY) {
            maxY = partCoord;
         }

         partCoord = partPos.getZ();
         if (partCoord < minZ) {
            minZ = partCoord;
         }

         if (partCoord > maxZ) {
            maxZ = partCoord;
         }
      }

      this.minimumCoord = new BlockPos(minX, minY, minZ);
      this.maximumCoord = new BlockPos(maxX, maxY, maxZ);
   }

   public BlockPos getMinimumCoord() {
      if (this.minimumCoord == null) {
         this.recalculateMinMaxCoords();
      }

      return this.minimumCoord;
   }

   public BlockPos getMaximumCoord() {
      if (this.maximumCoord == null) {
         this.recalculateMinMaxCoords();
      }

      return this.maximumCoord;
   }

   public boolean isEmpty() {
      return this.connectedParts.isEmpty();
   }

   public boolean shouldConsume(MultiblockControllerBase otherController) {
      if (!otherController.getClass().equals(this.getClass())) {
         throw new IllegalArgumentException("Attempting to merge two multiblocks with different master classes - this should never happen!");
      } else if (otherController == this) {
         return false;
      } else {
         int res = this._shouldConsume(otherController);
         if (res < 0) {
            return true;
         } else if (res > 0) {
            return false;
         } else {
            Base.instance.getLogger().warning(String.format("[%s] Encountered two controllers with the same reference coordinate. Auditing connected parts and retrying.", this.WORLD.isRemote ? "CLIENT" : "SERVER"));
            this.auditParts();
            otherController.auditParts();
            res = this._shouldConsume(otherController);
            if (res < 0) {
               return true;
            } else if (res > 0) {
               return false;
            } else {
               Base.instance.getLogger().fatal(String.format("My Controller (%d): size (%d), parts: %s", this.hashCode(), this.connectedParts.size(), this.getPartsListString()));
               Base.instance.getLogger().fatal(String.format("Other Controller (%d): size (%d), coords: %s", otherController.hashCode(), otherController.connectedParts.size(), otherController.getPartsListString()));
               throw new IllegalArgumentException("[" + (this.WORLD.isRemote ? "CLIENT" : "SERVER") + "] Two controllers with the same reference coord that somehow both have valid parts - this should never happen!");
            }
         }
      }
   }

   private int _shouldConsume(MultiblockControllerBase otherController) {
      BlockPos myCoord = this.getReferenceCoord();
      BlockPos theirCoord = otherController.getReferenceCoord();
      return theirCoord == null ? -1 : myCoord.compareTo(theirCoord);
   }

   private String getPartsListString() {
      StringBuilder sb = new StringBuilder();
      boolean first = true;

      for(IMultiblockPart part : this.connectedParts) {
         if (!first) {
            sb.append(", ");
         }

         BlockPos partPos = part.getWorldPosition();
         sb.append(String.format("(%d: %d, %d, %d)", part.hashCode(), partPos.getX(), partPos.getY(), partPos.getZ()));
         first = false;
      }

      return sb.toString();
   }

   private void auditParts() {
      HashSet<IMultiblockPart> deadParts = new HashSet();

      for(IMultiblockPart part : this.connectedParts) {
         if (part.isPartInvalid() || this.WORLD.getTileEntity(part.getWorldPosition()) != part) {
            this.onDetachBlock(part);
            deadParts.add(part);
         }
      }

      this.connectedParts.removeAll(deadParts);
      Base.instance.getLogger().warning(String.format("[%s] Controller found %d dead parts during an audit, %d parts remain attached", this.WORLD.isRemote ? "CLIENT" : "SERVER", deadParts.size(), this.connectedParts.size()));
   }

   public Set<IMultiblockPart> checkForDisconnections() {
      if (!this.shouldCheckForDisconnections) {
         return null;
      } else if (this.isEmpty()) {
         REGISTRY.addDeadController(this.WORLD, this);
         return null;
      } else {
         this.referenceCoord = null;
         Set<IMultiblockPart> deadParts = new HashSet();
         IMultiblockPart referencePart = null;
         int originalSize = this.connectedParts.size();

         for(IMultiblockPart part : this.connectedParts) {
            BlockPos position = part.getWorldPosition();
            if (this.WORLD.isBlockLoaded(position) && !part.isPartInvalid()) {
               if (this.WORLD.getTileEntity(position) != part) {
                  deadParts.add(part);
                  this.onDetachBlock(part);
               } else {
                  part.setUnvisited();
                  part.forfeitMultiblockSaveDelegate();
                  if (this.referenceCoord == null) {
                     this.referenceCoord = position;
                     referencePart = part;
                  } else if (position.compareTo(this.referenceCoord) < 0) {
                     this.referenceCoord = position;
                     referencePart = part;
                  }
               }
            } else {
               deadParts.add(part);
               this.onDetachBlock(part);
            }
         }

         this.connectedParts.removeAll(deadParts);
         deadParts.clear();
         if (referencePart != null && !this.isEmpty()) {
            referencePart.becomeMultiblockSaveDelegate();
            LinkedList<IMultiblockPart> partsToCheck = new LinkedList();
            IMultiblockPart[] nearbyParts = null;
            int visitedParts = 0;
            partsToCheck.add(referencePart);

            while(!partsToCheck.isEmpty()) {
               IMultiblockPart part = (IMultiblockPart)partsToCheck.removeFirst();
               part.setVisited();
               ++visitedParts;
               nearbyParts = part.getNeighboringParts();

               for(IMultiblockPart nearbyPart : nearbyParts) {
                  if (nearbyPart.getMultiblockController() == this && !nearbyPart.isVisited()) {
                     nearbyPart.setVisited();
                     partsToCheck.add(nearbyPart);
                  }
               }
            }

            Set<IMultiblockPart> removedParts = new HashSet();

            for(IMultiblockPart orphanCandidate : this.connectedParts) {
               if (!orphanCandidate.isVisited()) {
                  deadParts.add(orphanCandidate);
                  orphanCandidate.onOrphaned(this, originalSize, visitedParts);
                  this.onDetachBlock(orphanCandidate);
                  removedParts.add(orphanCandidate);
               }
            }

            this.connectedParts.removeAll(deadParts);
            deadParts.clear();
            if (this.referenceCoord == null) {
               this.selectNewReferenceCoord();
            }

            this.shouldCheckForDisconnections = false;
            return removedParts;
         } else {
            this.shouldCheckForDisconnections = false;
            REGISTRY.addDeadController(this.WORLD, this);
            return null;
         }
      }
   }

   public Set<IMultiblockPart> detachAllBlocks() {
      if (this.WORLD == null) {
         return new HashSet();
      } else {
         for(IMultiblockPart part : this.connectedParts) {
            if (this.WORLD.isBlockLoaded(part.getWorldPosition())) {
               this.onDetachBlock(part);
            }
         }

         Set<IMultiblockPart> detachedParts = this.connectedParts;
         this.connectedParts = new HashSet();
         return detachedParts;
      }
   }

   public boolean isAssembled() {
      return this.assemblyState == MultiblockControllerBase.AssemblyState.Assembled;
   }

   private void selectNewReferenceCoord() {
      IMultiblockPart theChosenOne = null;
      this.referenceCoord = null;

      for(IMultiblockPart part : this.connectedParts) {
         BlockPos position = part.getWorldPosition();
         if (!part.isPartInvalid() && this.WORLD.isBlockLoaded(position) && (this.referenceCoord == null || this.referenceCoord.compareTo(position) > 0)) {
            this.referenceCoord = position;
            theChosenOne = part;
         }
      }

      if (theChosenOne != null) {
         theChosenOne.becomeMultiblockSaveDelegate();
      }

   }

   protected void markReferenceCoordForUpdate() {
      BlockPos rc = this.getReferenceCoord();
      if (this.WORLD != null && rc != null) {
         Utils.notifyBlockUpdate(this.WORLD, rc, (IBlockState)null, (IBlockState)null);
      }

   }

   protected void markReferenceCoordDirty() {
      if (this.WORLD != null && !this.WORLD.isRemote) {
         BlockPos referenceCoord = this.getReferenceCoord();
         if (referenceCoord != null) {
            TileEntity saveTe = this.WORLD.getTileEntity(referenceCoord);
            this.WORLD.markChunkDirty(referenceCoord, saveTe);
         }
      }
   }

   protected void markMultiblockForRenderUpdate() {
      this.WORLD.markBlockRangeForRenderUpdate(this.getMinimumCoord(), this.getMaximumCoord());
   }

   public abstract void writeToDisk(NBTTagCompound var1);

   public abstract void readFromDisk(NBTTagCompound var1);

   protected static enum AssemblyState {
      Disassembled,
      Assembled,
      Paused;

      private AssemblyState() {
      }
   }
}
