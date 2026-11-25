package nightkosh.advanced_fishing.core;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import nightkosh.advanced_fishing.api.AdvancedFishingAPI;
import nightkosh.advanced_fishing.item.ItemBlazingFishingPole;
import nightkosh.advanced_fishing.item.ItemFish;

@ObjectHolder("advanced-fishing")
public class Items {
   public static final Item FISH = new ItemFish();
   public static final Item BLAZING_FISHING_POLE = new ItemBlazingFishingPole();

   public Items() {
   }

   @EventBusSubscriber(
      modid = "advanced-fishing"
   )
   public static class RegistrationHandler {
      public RegistrationHandler() {
      }

      @SubscribeEvent
      public static void registerItems(Register<Item> event) {
         event.getRegistry().register(Items.BLAZING_FISHING_POLE);
         event.getRegistry().register(Items.FISH);
         AdvancedFishingAPI.FISH_ITEM = Items.FISH;
      }
   }
}
