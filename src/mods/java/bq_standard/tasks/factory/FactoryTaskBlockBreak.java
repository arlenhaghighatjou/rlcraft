package bq_standard.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.tasks.TaskBlockBreak;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskBlockBreak implements IFactoryData<ITask, NBTTagCompound> {
   public static final FactoryTaskBlockBreak INSTANCE = new FactoryTaskBlockBreak();
   private final ResourceLocation REG_ID = new ResourceLocation("bq_standard", "block_break");

   public FactoryTaskBlockBreak() {
   }

   public ResourceLocation getRegistryName() {
      return this.REG_ID;
   }

   public TaskBlockBreak createNew() {
      return new TaskBlockBreak();
   }

   public TaskBlockBreak loadFromData(NBTTagCompound json) {
      TaskBlockBreak task = new TaskBlockBreak();
      task.readFromNBT(json);
      return task;
   }
}
