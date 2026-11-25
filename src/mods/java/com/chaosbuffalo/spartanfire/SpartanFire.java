package com.chaosbuffalo.spartanfire;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "spartanfire",
   name = "Spartan Fire",
   version = "1.1.0",
   dependencies = "required-after:iceandfire;required-after:spartanweaponry@[1.5.1,);required-after:llibrary@[1.7.19,)"
)
public class SpartanFire {
   public static final String MODID = "spartanfire";
   public static final String NAME = "Spartan Fire";
   public static final String VERSION = "1.1.0";
   private static Logger logger;

   public SpartanFire() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
   }
}
