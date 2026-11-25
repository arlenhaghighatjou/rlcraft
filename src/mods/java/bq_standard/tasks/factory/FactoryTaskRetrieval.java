package bq_standard.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.tasks.TaskRetrieval;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskRetrieval implements IFactoryData<ITask, NBTTagCompound> {
   public static final FactoryTaskRetrieval INSTANCE = new FactoryTaskRetrieval();

   public FactoryTaskRetrieval() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("bq_standard:retrieval");
   }

   public TaskRetrieval createNew() {
      return new TaskRetrieval();
   }

   public TaskRetrieval loadFromData(NBTTagCompound json) {
      TaskRetrieval task = new TaskRetrieval();
      task.readFromNBT(json);
      return task;
   }
}
