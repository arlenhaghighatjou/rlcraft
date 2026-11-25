package bq_standard.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.tasks.TaskScoreboard;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskScoreboard implements IFactoryData<ITask, NBTTagCompound> {
   public static final FactoryTaskScoreboard INSTANCE = new FactoryTaskScoreboard();

   public FactoryTaskScoreboard() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("bq_standard:scoreboard");
   }

   public TaskScoreboard createNew() {
      return new TaskScoreboard();
   }

   public TaskScoreboard loadFromData(NBTTagCompound json) {
      TaskScoreboard task = new TaskScoreboard();
      task.readFromNBT(json);
      return task;
   }
}
