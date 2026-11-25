package bq_standard.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.tasks.TaskHunt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskHunt implements IFactoryData<ITask, NBTTagCompound> {
   public static final FactoryTaskHunt INSTANCE = new FactoryTaskHunt();

   public FactoryTaskHunt() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("bq_standard:hunt");
   }

   public TaskHunt createNew() {
      return new TaskHunt();
   }

   public TaskHunt loadFromData(NBTTagCompound json) {
      TaskHunt task = new TaskHunt();
      task.readFromNBT(json);
      return task;
   }
}
