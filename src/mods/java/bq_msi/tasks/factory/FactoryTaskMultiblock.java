package bq_msi.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_msi.tasks.TaskMultiblock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskMultiblock implements IFactoryData<ITask, NBTTagCompound> {
   public static final FactoryTaskMultiblock INSTANCE = new FactoryTaskMultiblock();

   public FactoryTaskMultiblock() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("bq_msi:multiblock");
   }

   public TaskMultiblock createNew() {
      return new TaskMultiblock();
   }

   public TaskMultiblock loadFromData(NBTTagCompound json) {
      TaskMultiblock task = new TaskMultiblock();
      task.readFromNBT(json);
      return task;
   }
}
