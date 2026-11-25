package io.github.notstirred.mixincompat;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "mixincompat",
   name = "Mixin Compatibility",
   version = "1.0-SNAPSHOT"
)
public class MixinCompat {
   public static final String MOD_ID = "mixincompat";
   public static final String MOD_NAME = "Mixin Compatibility";
   public static final String VERSION = "1.0-SNAPSHOT";
   public static Logger LOGGER;

   public MixinCompat() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      LOGGER = event.getModLog();
      LOGGER.error("MIXINCOMPAT LOADED");
   }
}
