package bq_standard.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.tasks.TaskTrigger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskTrigger implements IFactoryData<ITask, NBTTagCompound> {
   public static final FactoryTaskTrigger INSTANCE = new FactoryTaskTrigger();
   private final ResourceLocation REG_ID = new ResourceLocation("bq_standard", "trigger");

   public FactoryTaskTrigger() {
   }

   public ResourceLocation getRegistryName() {
      return this.REG_ID;
   }

   public TaskTrigger createNew() {
      return new TaskTrigger();
   }

   public TaskTrigger loadFromData(NBTTagCompound nbt) {
      TaskTrigger task = new TaskTrigger();
      task.readFromNBT(nbt);
      return task;
   }
}
