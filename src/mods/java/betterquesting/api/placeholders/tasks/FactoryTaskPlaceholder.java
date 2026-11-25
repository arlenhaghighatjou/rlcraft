package betterquesting.api.placeholders.tasks;

import betterquesting.api2.registry.IFactoryData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskPlaceholder implements IFactoryData<TaskPlaceholder, NBTTagCompound> {
   public static final FactoryTaskPlaceholder INSTANCE = new FactoryTaskPlaceholder();
   private final ResourceLocation ID = new ResourceLocation("betterquesting:placeholder");

   private FactoryTaskPlaceholder() {
   }

   public ResourceLocation getRegistryName() {
      return this.ID;
   }

   public TaskPlaceholder createNew() {
      return new TaskPlaceholder();
   }

   public TaskPlaceholder loadFromData(NBTTagCompound nbt) {
      TaskPlaceholder task = this.createNew();
      task.readFromNBT(nbt);
      return task;
   }
}
