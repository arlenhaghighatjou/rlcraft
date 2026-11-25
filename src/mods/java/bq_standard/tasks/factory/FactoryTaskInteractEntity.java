package bq_standard.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.tasks.TaskInteractEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskInteractEntity implements IFactoryData<ITask, NBTTagCompound> {
   public static final FactoryTaskInteractEntity INSTANCE = new FactoryTaskInteractEntity();
   private final ResourceLocation REG_ID = new ResourceLocation("bq_standard", "interact_entity");

   public FactoryTaskInteractEntity() {
   }

   public ResourceLocation getRegistryName() {
      return this.REG_ID;
   }

   public TaskInteractEntity createNew() {
      return new TaskInteractEntity();
   }

   public TaskInteractEntity loadFromData(NBTTagCompound nbt) {
      TaskInteractEntity task = new TaskInteractEntity();
      task.readFromNBT(nbt);
      return task;
   }
}
