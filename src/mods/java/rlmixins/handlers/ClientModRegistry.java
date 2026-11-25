package rlmixins.handlers;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(
   modid = "rlmixins",
   value = {Side.CLIENT}
)
public class ClientModRegistry {
   public ClientModRegistry() {
   }

   @SubscribeEvent
   public static void modelRegisterEvent(ModelRegistryEvent event) {
      registerModels(ModRegistry.scarliteHelmet, ModRegistry.scarliteChestplate, ModRegistry.scarliteLeggings, ModRegistry.scarliteBoots);
      registerModels(ModRegistry.steelHelmet, ModRegistry.steelChestplate, ModRegistry.steelLeggings, ModRegistry.steelBoots);
      registerModels(ModRegistry.cleansingTalisman);
   }

   private static void registerModels(Item... values) {
      for(Item entry : values) {
         ModelLoader.setCustomModelResourceLocation(entry, 0, new ModelResourceLocation(entry.getRegistryName(), "inventory"));
      }

   }
}
