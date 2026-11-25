package bq_standard.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.tasks.TaskCheckbox;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskCheckbox implements IFactoryData<ITask, NBTTagCompound> {
   public static final FactoryTaskCheckbox INSTANCE = new FactoryTaskCheckbox();

   public FactoryTaskCheckbox() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("bq_standard:checkbox");
   }

   public TaskCheckbox createNew() {
      return new TaskCheckbox();
   }

   public TaskCheckbox loadFromData(NBTTagCompound json) {
      TaskCheckbox task = new TaskCheckbox();
      task.readFromNBT(json);
      return task;
   }
}
