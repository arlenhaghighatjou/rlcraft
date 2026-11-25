package com.fantasticsource.mctools.data;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public abstract class CModpackDataHandler extends CDataHandler {
   public CModpackDataHandler() {
   }

   public static void load(FMLInitializationEvent event) {
      CDataHandler.load(CModpackDataHandler.class);
   }
}
