package bq_standard.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.tasks.TaskInteractItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskInteractItem implements IFactoryData<ITask, NBTTagCompound> {
   public static final FactoryTaskInteractItem INSTANCE = new FactoryTaskInteractItem();
   private final ResourceLocation REG_ID = new ResourceLocation("bq_standard", "interact_item");

   public FactoryTaskInteractItem() {
   }

   public ResourceLocation getRegistryName() {
      return this.REG_ID;
   }

   public TaskInteractItem createNew() {
      return new TaskInteractItem();
   }

   public TaskInteractItem loadFromData(NBTTagCompound nbt) {
      TaskInteractItem task = new TaskInteractItem();
      task.readFromNBT(nbt);
      return task;
   }
}
