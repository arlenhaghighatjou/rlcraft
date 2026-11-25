package bq_standard.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.tasks.TaskTame;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public final class FactoryTaskTame implements IFactoryData<ITask, NBTTagCompound> {
   public static final FactoryTaskTame INSTANCE = new FactoryTaskTame();
   private final ResourceLocation REG_ID = new ResourceLocation("bq_standard", "tame");

   public FactoryTaskTame() {
   }

   public ResourceLocation getRegistryName() {
      return this.REG_ID;
   }

   public TaskTame createNew() {
      return new TaskTame();
   }

   public TaskTame loadFromData(NBTTagCompound nbt) {
      TaskTame task = new TaskTame();
      task.readFromNBT(nbt);
      return task;
   }
}
