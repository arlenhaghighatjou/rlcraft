package lellson.roughTweaks.items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(
   modid = "roughtweaks"
)
public class RoughItemRegistry {
   public RoughItemRegistry() {
   }

   @SubscribeEvent
   public static void register(Register<Item> event) {
      IForgeRegistry<Item> registry = event.getRegistry();

      for(Item object : RoughItems.ITEMS) {
         registry.register(object);
      }

   }
}
