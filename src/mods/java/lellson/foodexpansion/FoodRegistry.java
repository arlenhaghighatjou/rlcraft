package lellson.foodexpansion;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class FoodRegistry {
   public FoodRegistry() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Item.class, FoodRegistry::register);
   }

   public static void register(Register<Item> event) {
      IForgeRegistry<Item> registry = event.getRegistry();

      for(Item object : FoodItems.ITEM_LIST) {
         registry.register(object);
      }

   }
}
