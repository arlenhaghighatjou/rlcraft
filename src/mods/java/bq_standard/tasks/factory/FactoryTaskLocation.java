package bq_standard.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.tasks.TaskLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskLocation implements IFactoryData<ITask, NBTTagCompound> {
   public static final FactoryTaskLocation INSTANCE = new FactoryTaskLocation();

   public FactoryTaskLocation() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("bq_standard:location");
   }

   public TaskLocation createNew() {
      return new TaskLocation();
   }

   public TaskLocation loadFromData(NBTTagCompound json) {
      TaskLocation task = new TaskLocation();
      task.readFromNBT(json);
      return task;
   }
}
