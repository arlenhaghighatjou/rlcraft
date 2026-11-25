package com.ferreusveritas.dynamictrees;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(
   modid = "dynamictrees"
)
public class ModModels {
   public ModModels() {
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void register(ModelRegistryEvent event) {
      DynamicTrees.proxy.registerModels();
   }
}
