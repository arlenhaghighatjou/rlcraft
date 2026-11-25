package goblinbob.mobends.core.module;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IModule {
   void preInit(FMLPreInitializationEvent var1);

   void onRefresh();
}
