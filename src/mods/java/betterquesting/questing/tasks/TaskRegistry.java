package betterquesting.questing.tasks;

import betterquesting.api.placeholders.tasks.FactoryTaskPlaceholder;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import betterquesting.api2.registry.IRegistry;
import betterquesting.core.BetterQuesting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

public class TaskRegistry implements IRegistry<IFactoryData<ITask, NBTTagCompound>, ITask> {
   public static final TaskRegistry INSTANCE = new TaskRegistry();
   private final HashMap<ResourceLocation, IFactoryData<ITask, NBTTagCompound>> taskRegistry = new HashMap();

   public TaskRegistry() {
   }

   public void register(IFactoryData<ITask, NBTTagCompound> factory) {
      if (factory == null) {
         throw new NullPointerException("Tried to register null task");
      } else if (factory.getRegistryName() == null) {
         throw new IllegalArgumentException("Tried to register a task with a null name: " + factory.getClass());
      } else if (!this.taskRegistry.containsKey(factory.getRegistryName()) && !this.taskRegistry.containsValue(factory)) {
         this.taskRegistry.put(factory.getRegistryName(), factory);
      } else {
         throw new IllegalArgumentException("Cannot register dupliate task type: " + factory.getRegistryName());
      }
   }

   public IFactoryData<ITask, NBTTagCompound> getFactory(ResourceLocation registryName) {
      return (IFactoryData)this.taskRegistry.get(registryName);
   }

   public List<IFactoryData<ITask, NBTTagCompound>> getAll() {
      return new ArrayList(this.taskRegistry.values());
   }

   public ITask createNew(ResourceLocation registryName) {
      try {
         IFactoryData<? extends ITask, NBTTagCompound> factory;
         if (FactoryTaskPlaceholder.INSTANCE.getRegistryName().equals(registryName)) {
            factory = FactoryTaskPlaceholder.INSTANCE;
         } else {
            factory = this.getFactory(registryName);
         }

         if (factory == null) {
            BetterQuesting.logger.log(Level.ERROR, "Tried to load missing task type '" + registryName + "'! Are you missing an expansion pack?");
            return null;
         } else {
            return (ITask)factory.createNew();
         }
      } catch (Exception var3) {
         BetterQuesting.logger.log(Level.ERROR, "Unable to instatiate task: " + registryName, var3);
         return null;
      }
   }
}
