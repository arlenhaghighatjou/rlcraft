package bq_msi.core.proxies;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import betterquesting.api2.registry.IRegistry;
import bq_msi.handlers.EventHandler;
import bq_msi.handlers.RecurrentHandler;
import bq_msi.tasks.factory.FactoryTaskMultiblock;
import bq_msi.tasks.factory.FactoryTaskRCLocate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

public class CommonProxy {
   public CommonProxy() {
   }

   public boolean isClient() {
      return false;
   }

   public void registerHandlers() {
      MinecraftForge.EVENT_BUS.register(new EventHandler());
      if (Loader.isModLoaded("reccomplex")) {
         MinecraftForge.EVENT_BUS.register(new RecurrentHandler());
      }

   }

   public void registerRenderers() {
   }

   public void registerExpansion() {
      IRegistry<IFactoryData<ITask, NBTTagCompound>, ITask> taskReg = (IRegistry)QuestingAPI.getAPI(ApiReference.TASK_REG);
      taskReg.register(FactoryTaskMultiblock.INSTANCE);
      if (Loader.isModLoaded("reccomplex")) {
         taskReg.register(FactoryTaskRCLocate.INSTANCE);
      }

   }
}
