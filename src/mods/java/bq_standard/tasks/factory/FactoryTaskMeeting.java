package bq_standard.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.tasks.TaskMeeting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskMeeting implements IFactoryData<ITask, NBTTagCompound> {
   public static final FactoryTaskMeeting INSTANCE = new FactoryTaskMeeting();

   public FactoryTaskMeeting() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("bq_standard:meeting");
   }

   public TaskMeeting createNew() {
      return new TaskMeeting();
   }

   public TaskMeeting loadFromData(NBTTagCompound json) {
      TaskMeeting task = new TaskMeeting();
      task.readFromNBT(json);
      return task;
   }
}
