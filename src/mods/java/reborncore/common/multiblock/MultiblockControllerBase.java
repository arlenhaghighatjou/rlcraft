package reborncore.common.multiblock;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import reborncore.RebornCore;
import reborncore.common.util.WorldUtils;

public abstract class MultiblockControllerBase {
   public static final short DIMENSION_UNBOUNDED = -1;
   protected World worldObj;
   protected MultiblockControllerBase.AssemblyState assemblyState;
   public HashSet<IMultiblockPart> connectedParts;
   private BlockPos referenceCoord;
   private BlockPos minimumCoord;
   private BlockPos maximumCoord;
   private boolean shouldCheckForDisconnections;
   private MultiblockValidationException lastValidationException;
   protected boolean debugMode;

   protected MultiblockControllerBase(World world) {
      this.worldObj = world;
      this.connectedParts = new HashSet();
      this.referenceCoord = null;
      this.assemblyState = MultiblockControllerBase.AssemblyState.Disassembled;
      this.minimumCoord = null;
      this.maximumCoord = null;
      this.shouldCheckForDisconnections = true;
      this.lastValidationException = null;
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
      return this.connectedParts.contains(blockCoord);
   }

   public void attachBlock(IMultiblockPart part) {
      BlockPos coord = part.getWorldLocation();
      if (!this.connectedParts.add(part)) {
         RebornCore.logHelper.warn(String.format("[%s] Controller %s is double-adding part %d @ %s. This is unusual. If you encounter odd behavior, please tear down the machine and rebuild it.", this.worldObj.isRemote ? "CLIENT" : "SERVER", this.hashCode(), part.hashCode(), coord));
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
         TileEntity te = this.worldObj.getTileEntity(this.referenceCoord);
         ((IMultiblockPart)te).forfeitMultiblockSaveDelegate();
         this.referenceCoord = coord;
         part.becomeMultiblockSaveDelegate();
      } else {
         part.forfeitMultiblockSaveDelegate();
      }

      Boolean updateRequired = false;
      BlockPos partPos = part.getPos();
      if (this.minimumCoord != null) {
         if (partPos.getX() < this.minimumCoord.getX()) {
            updateRequired = true;
         }

         if (partPos.getY() < this.minimumCoord.getY()) {
            updateRequired = true;
         }

         if (partPos.getZ() < this.minimumCoord.getZ()) {
            updateRequired = true;
         }

         if (updateRequired.booleanValue()) {
            this.minimumCoord = new BlockPos(partPos.getX(), partPos.getY(), partPos.getZ());
         }
      }

      if (this.maximumCoord != null) {
         if (partPos.getX() > this.maximumCoord.getX()) {
            updateRequired = true;
         }

         if (partPos.getY() > this.maximumCoord.getY()) {
            updateRequired = true;
         }

         if (partPos.getZ() > this.maximumCoord.getZ()) {
            updateRequired = true;
         }

         if (updateRequired.booleanValue()) {
            this.maximumCoord = new BlockPos(partPos.getX(), partPos.getY(), partPos.getZ());
         }
      }

      MultiblockRegistry.addDirtyController(this.worldObj, this);
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
      if (this.referenceCoord != null && this.referenceCoord.equals(part.getPos())) {
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
         RebornCore.logHelper.warn(String.format("[%s] Double-removing part (%d) @ %d, %d, %d, this is unexpected and may cause problems. If you encounter anomalies, please tear down the reactor and rebuild it.", this.worldObj.isRemote ? "CLIENT" : "SERVER", part.hashCode(), part.getPos().getX(), part.getPos().getY(), part.getPos().getZ()));
      }

      if (this.connectedParts.isEmpty()) {
         MultiblockRegistry.addDeadController(this.worldObj, this);
      } else {
         MultiblockRegistry.addDirtyController(this.worldObj, this);
         if (this.referenceCoord == null) {
            this.selectNewReferenceCoord();
         }

      }
   }

   protected abstract int getMinimumNumberOfBlocksForAssembledMachine();

   protected abstract int getMaximumXSize();

   protected abstract int getMaximumZSize();

   protected abstract int getMaximumYSize();

   protected int getMinimumXSize() {
      return 1;
   }

   protected int getMinimumYSize() {
      return 1;
   }

   protected int getMinimumZSize() {
      return 1;
   }

   public MultiblockValidationException getLastValidationException() {
      return this.lastValidationException;
   }

   protected abstract void isMachineWhole() throws MultiblockValidationException;

   public void checkIfMachineIsWhole() {
      MultiblockControllerBase.AssemblyState oldState = this.assemblyState;
      this.lastValidationException = null;

      boolean isWhole;
      try {
         this.isMachineWhole();
         isWhole = true;
      } catch (MultiblockValidationException var4) {
         this.lastValidationException = var4;
         isWhole = false;
      }

      if (isWhole) {
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
            if (!acquiredPart.isInvalid()) {
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
         if (this.worldObj.isBlockLoaded(this.referenceCoord)) {
            TileEntity te = this.worldObj.getTileEntity(this.referenceCoord);
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
         MultiblockRegistry.addDeadController(this.worldObj, this);
      } else if (this.assemblyState == MultiblockControllerBase.AssemblyState.Assembled) {
         if (this.worldObj.isRemote) {
            this.updateClient();
         } else if (this.updateServer() && this.minimumCoord != null && this.maximumCoord != null && this.worldObj.isAreaLoaded(this.minimumCoord, this.maximumCoord)) {
            int minChunkX = this.minimumCoord.getX() >> 4;
            int minChunkZ = this.minimumCoord.getZ() >> 4;
            int maxChunkX = this.maximumCoord.getX() >> 4;
            int maxChunkZ = this.maximumCoord.getZ() >> 4;

            for(int x = minChunkX; x <= maxChunkX; ++x) {
               for(int z = minChunkZ; z <= maxChunkZ; ++z) {
                  Chunk chunkToSave = this.worldObj.getChunkFromChunkCoords(x, z);
                  chunkToSave.markDirty();
               }
            }
         }

      }
   }

   protected abstract boolean updateServer();

   protected abstract void updateClient();

   protected void isBlockGoodForFrame(World world, int x, int y, int z) throws MultiblockValidationException {
      throw new MultiblockValidationException(String.format("%d, %d, %d - Block is not valid for use in the machine's interior", x, y, z));
   }

   protected void isBlockGoodForTop(World world, int x, int y, int z) throws MultiblockValidationException {
      throw new MultiblockValidationException(String.format("%d, %d, %d - Block is not valid for use in the machine's interior", x, y, z));
   }

   protected void isBlockGoodForBottom(World world, int x, int y, int z) throws MultiblockValidationException {
      throw new MultiblockValidationException(String.format("%d, %d, %d - Block is not valid for use in the machine's interior", x, y, z));
   }

   protected void isBlockGoodForSides(World world, int x, int y, int z) throws MultiblockValidationException {
      throw new MultiblockValidationException(String.format("%d, %d, %d - Block is not valid for use in the machine's interior", x, y, z));
   }

   protected void isBlockGoodForInterior(World world, int x, int y, int z) throws MultiblockValidationException {
      throw new MultiblockValidationException(String.format("%d, %d, %d - Block is not valid for use in the machine's interior", x, y, z));
   }

   public BlockPos getReferenceCoord() {
      if (this.referenceCoord == null) {
         this.selectNewReferenceCoord();
      }

      return this.referenceCoord;
   }

   public int getNumConnectedBlocks() {
      return this.connectedParts.size();
   }

   public abstract void writeToNBT(NBTTagCompound var1);

   public abstract void readFromNBT(NBTTagCompound var1);

   public void recalculateMinMaxCoords() {
      Integer minY;
      Integer minZ;
      Integer minX = minY = minZ = Integer.MAX_VALUE;
      Integer maxY;
      Integer maxZ;
      Integer maxX = maxY = maxZ = Integer.MIN_VALUE;

      for(IMultiblockPart part : this.connectedParts) {
         BlockPos pos = part.getPos();
         if (pos.getX() < minX.intValue()) {
            minX = pos.getX();
         }

         if (pos.getX() > maxX.intValue()) {
            maxX = pos.getX();
         }

         if (pos.getY() < minY.intValue()) {
            minY = pos.getY();
         }

         if (pos.getY() > maxY.intValue()) {
            maxY = pos.getY();
         }

         if (pos.getZ() < minZ.intValue()) {
            minZ = pos.getZ();
         }

         if (pos.getZ() > maxZ.intValue()) {
            maxZ = pos.getZ();
         }
      }

      this.minimumCoord = new BlockPos(minX.intValue(), minY.intValue(), minZ.intValue());
      this.maximumCoord = new BlockPos(maxX.intValue(), maxY.intValue(), maxZ.intValue());
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

   public abstract void formatDescriptionPacket(NBTTagCompound var1);

   public abstract void decodeDescriptionPacket(NBTTagCompound var1);

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
            RebornCore.logHelper.warn(String.format("[%s] Encountered two controllers with the same reference coordinate. Auditing connected parts and retrying.", this.worldObj.isRemote ? "CLIENT" : "SERVER"));
            this.auditParts();
            otherController.auditParts();
            res = this._shouldConsume(otherController);
            if (res < 0) {
               return true;
            } else if (res > 0) {
               return false;
            } else {
               RebornCore.logHelper.error(String.format("My Controller (%d): size (%d), parts: %s", this.hashCode(), this.connectedParts.size(), this.getPartsListString()));
               RebornCore.logHelper.error(String.format("Other Controller (%d): size (%d), coords: %s", otherController.hashCode(), otherController.connectedParts.size(), otherController.getPartsListString()));
               throw new IllegalArgumentException("[" + (this.worldObj.isRemote ? "CLIENT" : "SERVER") + "] Two controllers with the same reference coord that somehow both have valid parts - this should never happen!");
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

         sb.append(String.format("(%d: %d, %d, %d)", part.hashCode(), part.getPos().getX(), part.getPos().getY(), part.getPos().getZ()));
         first = false;
      }

      return sb.toString();
   }

   private void auditParts() {
      HashSet<IMultiblockPart> deadParts = new HashSet();

      for(IMultiblockPart part : this.connectedParts) {
         if (part.isInvalid() || this.worldObj.getTileEntity(part.getPos()) != part) {
            this.onDetachBlock(part);
            deadParts.add(part);
         }
      }

      this.connectedParts.removeAll(deadParts);
      RebornCore.logHelper.warn(String.format("[%s] Controller found %d dead parts during an audit, %d parts remain attached", this.worldObj.isRemote ? "CLIENT" : "SERVER", deadParts.size(), this.connectedParts.size()));
   }

   public Set<IMultiblockPart> checkForDisconnections() {
      if (!this.shouldCheckForDisconnections) {
         return null;
      } else if (this.isEmpty()) {
         MultiblockRegistry.addDeadController(this.worldObj, this);
         return null;
      } else {
         this.referenceCoord = null;
         Set<IMultiblockPart> deadParts = new HashSet();
         IMultiblockPart referencePart = null;
         int originalSize = this.connectedParts.size();

         for(IMultiblockPart part : this.connectedParts) {
            BlockPos pos = part.getWorldLocation();
            if (this.worldObj.isBlockLoaded(pos) && !part.isInvalid()) {
               if (this.worldObj.getTileEntity(pos) != part) {
                  deadParts.add(part);
                  this.onDetachBlock(part);
               } else {
                  part.setUnvisited();
                  part.forfeitMultiblockSaveDelegate();
                  if (this.referenceCoord == null) {
                     this.referenceCoord = pos;
                     referencePart = part;
                  } else if (pos.compareTo(this.referenceCoord) < 0) {
                     this.referenceCoord = pos;
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
            MultiblockRegistry.addDeadController(this.worldObj, this);
            return null;
         }
      }
   }

   public Set<IMultiblockPart> detachAllBlocks() {
      if (this.worldObj == null) {
         return new HashSet();
      } else {
         for(IMultiblockPart part : this.connectedParts) {
            if (this.worldObj.isBlockLoaded(part.getWorldLocation())) {
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
         BlockPos pos = part.getWorldLocation();
         if (!part.isInvalid() && this.worldObj.isBlockLoaded(pos) && (this.referenceCoord == null || this.referenceCoord.compareTo(pos) > 0)) {
            this.referenceCoord = pos;
            theChosenOne = part;
         }
      }

      if (theChosenOne != null) {
         theChosenOne.becomeMultiblockSaveDelegate();
      }

   }

   protected void markReferenceCoordForUpdate() {
      BlockPos rc = this.getReferenceCoord();
      if (this.worldObj != null && rc != null) {
         WorldUtils.updateBlock(this.worldObj, rc);
      }

   }

   protected void markReferenceCoordDirty() {
      if (this.worldObj != null && !this.worldObj.isRemote) {
         BlockPos referenceCoord = this.getReferenceCoord();
         if (referenceCoord != null) {
            TileEntity saveTe = this.worldObj.getTileEntity(referenceCoord);
            this.worldObj.markChunkDirty(referenceCoord, saveTe);
         }
      }
   }

   protected static enum AssemblyState {
      Disassembled,
      Assembled,
      Paused;

      private AssemblyState() {
      }
   }
}
