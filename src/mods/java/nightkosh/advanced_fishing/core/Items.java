package nightkosh.advanced_fishing.core;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import nightkosh.advanced_fishing.api.AdvancedFishingAPI;
import nightkosh.advanced_fishing.item.ItemBlazingFishingPole;
import nightkosh.advanced_fishing.item.ItemFish;

public class Items {
   public static final Item FISH = new ItemFish();
   public static final Item BLAZING_FISHING_POLE = new ItemBlazingFishingPole();

   public Items() {
   }

   public static class RegistrationHandler {
      public RegistrationHandler() {
      }

      public static void register(EventBus bus) {
         bus.registerGeneric(Register.class, Item.class, RegistrationHandler::registerItems);
      }

      public static void registerItems(Register<Item> event) {
         event.getRegistry().register(Items.BLAZING_FISHING_POLE);
         event.getRegistry().register(Items.FISH);
         AdvancedFishingAPI.FISH_ITEM = Items.FISH;
      }
   }
}
