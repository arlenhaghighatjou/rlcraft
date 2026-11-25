package betterquesting.api.questing.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api2.storage.DBEntry;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidTask extends ITask {
   boolean canAcceptFluid(EaglercraftUUID var1, DBEntry<IQuest> var2, FluidStack var3);

   FluidStack submitFluid(EaglercraftUUID var1, DBEntry<IQuest> var2, FluidStack var3);
}
