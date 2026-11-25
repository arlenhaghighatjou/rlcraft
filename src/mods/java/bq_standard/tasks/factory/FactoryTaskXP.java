package bq_standard.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.tasks.TaskXP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskXP implements IFactoryData<ITask, NBTTagCompound> {
   public static final FactoryTaskXP INSTANCE = new FactoryTaskXP();

   public FactoryTaskXP() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("bq_standard:xp");
   }

   public TaskXP createNew() {
      return new TaskXP();
   }

   public TaskXP loadFromData(NBTTagCompound json) {
      TaskXP task = new TaskXP();
      task.readFromNBT(json);
      return task;
   }
}
