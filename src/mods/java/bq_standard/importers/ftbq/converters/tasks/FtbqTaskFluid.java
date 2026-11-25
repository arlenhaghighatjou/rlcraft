package bq_standard.importers.ftbq.converters.tasks;

import betterquesting.api.placeholders.PlaceholderConverter;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import bq_standard.importers.ftbq.FTBQQuestImporter;
import bq_standard.tasks.TaskFluid;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FtbqTaskFluid {
   public FtbqTaskFluid() {
   }

   public ITask[] convertTask(NBTTagCompound nbt) {
      String fName = nbt.getString("fluid");
      Fluid fluid = FluidRegistry.getFluid(fName);
      long amount = nbt.getLong("amount");
      NBTTagCompound tag = !nbt.hasKey("tag", 10) ? null : nbt.getCompoundTag("tag");
      FluidStack stack = PlaceholderConverter.convertFluid(fluid, fName, 1, tag);
      TaskFluid task = new TaskFluid();

      int split;
      for(long rem = amount; rem > 0L; rem -= (long)split) {
         split = (int)(rem % 2147483647L);
         stack.amount = split;
         task.requiredFluids.add(stack.copy());
      }

      FTBQQuestImporter.provideQuestIcon(new BigItemStack(Items.BUCKET));
      return new ITask[]{task};
   }
}
