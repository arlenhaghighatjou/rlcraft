package se.gory_moon.globalgamerules.proxy;

import net.minecraftforge.common.MinecraftForge;
import se.gory_moon.globalgamerules.GlobalGR;

public abstract class CommonProxy {
   public CommonProxy() {
   }

   public abstract void setConfigEntryClasses();

   public void registerEvents() {
      GlobalGR.getConfig().register(MinecraftForge.EVENT_BUS);
   }
}
