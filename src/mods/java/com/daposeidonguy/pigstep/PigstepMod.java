package com.daposeidonguy.pigstep;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.Mod;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "pigstep",
   name = "Pigstep",
   version = "1.12",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class PigstepMod {
   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         com.daposeidonguy.pigstep.common.LootTableHandler.register(MinecraftForge.EVENT_BUS);
         com.daposeidonguy.pigstep.common.items.ItemHandler.register(MinecraftForge.EVENT_BUS);
      }

   }

   public static final String MODID = "pigstep";
   public static final String NAME = "Pigstep";
   public static final String VERSION = "1.12";
   public static Logger logger = LogManager.getLogger();

   public PigstepMod() {
   }
}
