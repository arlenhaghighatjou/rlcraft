package reborncore.common.multiblock;

import java.util.Set;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public abstract class IMultiblockPart extends TileEntity {
   public static final int INVALID_DISTANCE = Integer.MAX_VALUE;

   public IMultiblockPart() {
   }

   public abstract boolean isConnected();

   public abstract MultiblockControllerBase getMultiblockController();

   public abstract BlockPos getWorldLocation();

   public abstract void onAttached(MultiblockControllerBase var1);

   public abstract void onDetached(MultiblockControllerBase var1);

   public abstract void onOrphaned(MultiblockControllerBase var1, int var2, int var3);

   public abstract MultiblockControllerBase createNewMultiblock();

   public abstract Class<? extends MultiblockControllerBase> getMultiblockControllerType();

   public abstract void onAssimilated(MultiblockControllerBase var1);

   public abstract void setVisited();

   public abstract void setUnvisited();

   public abstract boolean isVisited();

   public abstract void becomeMultiblockSaveDelegate();

   public abstract void forfeitMultiblockSaveDelegate();

   public abstract boolean isMultiblockSaveDelegate();

   public abstract IMultiblockPart[] getNeighboringParts();

   public abstract void onMachineAssembled(MultiblockControllerBase var1);

   public abstract void onMachineBroken();

   public abstract void onMachineActivated();

   public abstract void onMachineDeactivated();

   public abstract Set<MultiblockControllerBase> attachToNeighbors();

   public abstract void assertDetached();

   public abstract boolean hasMultiblockSaveData();

   public abstract NBTTagCompound getMultiblockSaveData();

   public abstract void onMultiblockDataAssimilated();

   public abstract IBlockState getBlockState();
}
