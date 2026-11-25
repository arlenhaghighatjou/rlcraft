package bq_standard.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.tasks.TaskFluid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskFluid implements IFactoryData<ITask, NBTTagCompound> {
   public static final FactoryTaskFluid INSTANCE = new FactoryTaskFluid();

   public FactoryTaskFluid() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("bq_standard:fluid");
   }

   public TaskFluid createNew() {
      return new TaskFluid();
   }

   public TaskFluid loadFromData(NBTTagCompound json) {
      TaskFluid task = new TaskFluid();
      task.readFromNBT(json);
      return task;
   }
}
