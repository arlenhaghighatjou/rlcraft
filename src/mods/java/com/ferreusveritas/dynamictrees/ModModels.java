package com.ferreusveritas.dynamictrees;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModModels {
   public ModModels() {
   }

   public static void register(EventBus bus) {
      bus.register(ModelRegistryEvent.class, ModModels::register);
   }

   @SideOnly(Side.CLIENT)
   public static void register(ModelRegistryEvent event) {
      DynamicTrees.proxy.registerModels();
   }
}
