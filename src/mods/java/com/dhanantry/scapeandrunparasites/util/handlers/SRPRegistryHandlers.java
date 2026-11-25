package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.world.gen.WorldGenCustomStructures;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SRPRegistryHandlers {
   public SRPRegistryHandlers() {
   }

   public static void initRenders() {
      SRPRenderHandler.registryEntityRenders();
   }

   public static void initEvents() {
      MinecraftForge.EVENT_BUS.register(new SRPEventHandlerBus());
      MinecraftForge.TERRAIN_GEN_BUS.register(new SRPEventHandlerTerrainGen());
      GameRegistry.registerWorldGenerator(new WorldGenCustomStructures(), 0);
   }
}
