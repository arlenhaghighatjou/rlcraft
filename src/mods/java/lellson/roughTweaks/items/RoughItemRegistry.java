package lellson.roughTweaks.items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.registries.IForgeRegistry;

public class RoughItemRegistry {
   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Item.class, RoughItemRegistry::register);
   }

   public static void register(Register<Item> event) {
      IForgeRegistry<Item> registry = event.getRegistry();

      for(Item object : RoughItems.ITEMS) {
         registry.register(object);
      }

   }
}
