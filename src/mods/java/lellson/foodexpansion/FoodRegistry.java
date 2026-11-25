package lellson.foodexpansion;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(
   modid = "foodexpansion"
)
public class FoodRegistry {
   public FoodRegistry() {
   }

   @SubscribeEvent
   public static void register(Register<Item> event) {
      IForgeRegistry<Item> registry = event.getRegistry();

      for(Item object : FoodItems.ITEM_LIST) {
         registry.register(object);
      }

   }
}
