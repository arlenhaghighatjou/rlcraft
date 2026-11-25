package betterquesting.api.questing.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api2.storage.DBEntry;
import java.util.UUID;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidTask extends ITask {
   boolean canAcceptFluid(UUID var1, DBEntry<IQuest> var2, FluidStack var3);

   FluidStack submitFluid(UUID var1, DBEntry<IQuest> var2, FluidStack var3);
}
