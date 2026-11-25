package svenhjol.charm.base;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import svenhjol.meson.MesonLoader;

public final class CharmLoader extends MesonLoader {
   public static final MesonLoader INSTANCE = new CharmLoader();

   public CharmLoader() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      CharmSounds.registerSounds();
      CharmLootTables.registerLootTables();
      super.preInit(event);
   }
}
