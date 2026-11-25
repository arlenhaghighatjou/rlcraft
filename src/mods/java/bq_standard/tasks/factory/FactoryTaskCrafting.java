package bq_standard.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.tasks.TaskCrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskCrafting implements IFactoryData<ITask, NBTTagCompound> {
   public static final FactoryTaskCrafting INSTANCE = new FactoryTaskCrafting();

   public FactoryTaskCrafting() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("bq_standard:crafting");
   }

   public TaskCrafting createNew() {
      return new TaskCrafting();
   }

   public TaskCrafting loadFromData(NBTTagCompound json) {
      TaskCrafting task = new TaskCrafting();
      task.readFromNBT(json);
      return task;
   }
}
