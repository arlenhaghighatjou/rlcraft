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
      (new SRPEventHandlerBus()).register(MinecraftForge.EVENT_BUS);
      (new SRPEventHandlerTerrainGen()).register(MinecraftForge.TERRAIN_GEN_BUS);
      GameRegistry.registerWorldGenerator(new WorldGenCustomStructures(), 0);
   }
}
