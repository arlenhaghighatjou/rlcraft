package codersafterdark.compatskills.common.compats.betterquesting;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskRequirement implements IFactoryData<ITask, NBTTagCompound> {
   public static final FactoryTaskRequirement INSTANCE = new FactoryTaskRequirement();

   public FactoryTaskRequirement() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("compatskills", "req_task_factory");
   }

   public TaskRequirement createNew() {
      return new TaskRequirement();
   }

   public TaskRequirement loadFromData(NBTTagCompound json) {
      TaskRequirement task = this.createNew();
      task.readFromNBT(json);
      return task;
   }
}
