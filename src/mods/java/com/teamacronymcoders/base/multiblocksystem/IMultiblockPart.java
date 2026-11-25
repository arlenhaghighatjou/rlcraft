package com.teamacronymcoders.base.multiblocksystem;

import java.util.Set;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public interface IMultiblockPart {
   boolean isConnected();

   MultiblockControllerBase getMultiblockController();

   BlockPos getWorldPosition();

   boolean isPartInvalid();

   void onAttached(MultiblockControllerBase var1);

   void onDetached(MultiblockControllerBase var1);

   void onOrphaned(MultiblockControllerBase var1, int var2, int var3);

   MultiblockControllerBase createNewMultiblock();

   Class<? extends MultiblockControllerBase> getMultiblockControllerType();

   void onAssimilated(MultiblockControllerBase var1);

   void setVisited();

   void setUnvisited();

   boolean isVisited();

   void becomeMultiblockSaveDelegate();

   void forfeitMultiblockSaveDelegate();

   boolean isMultiblockSaveDelegate();

   IMultiblockPart[] getNeighboringParts();

   void onMachineAssembled(MultiblockControllerBase var1);

   void onMachineBroken();

   void onMachineActivated();

   void onMachineDeactivated();

   Set<MultiblockControllerBase> attachToNeighbors();

   void assertDetached();

   boolean hasMultiblockSaveData();

   NBTTagCompound getMultiblockSaveData();

   void onMultiblockDataAssimilated();
}
