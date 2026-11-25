package de.mennomax.astikorcarts.handler;

import com.google.common.collect.UnmodifiableIterator;
import de.mennomax.astikorcarts.entity.EntityCargoCart;
import de.mennomax.astikorcarts.entity.EntityPlowCart;
import de.mennomax.astikorcarts.init.ModItems;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@EventBusSubscriber(
   modid = "astikorcarts"
)
public class MissingMappingHandler {
   public MissingMappingHandler() {
   }

   @SubscribeEvent
   public static void onMissingItemMapping(MissingMappings<Item> event) {
      UnmodifiableIterator var1 = event.getAllMappings().iterator();

      while(var1.hasNext()) {
         Mapping<Item> mapping = (Mapping)var1.next();
         String var3 = mapping.key.toString();
         byte var4 = -1;
         switch(var3.hashCode()) {
         case -1894862512:
            if (var3.equals("astikoor:cargocart")) {
               var4 = 1;
            }
            break;
         case -1351638577:
            if (var3.equals("astikoor:wheel")) {
               var4 = 2;
            }
            break;
         case -1103426800:
            if (var3.equals("astikoor:plowcart")) {
               var4 = 0;
            }
         }

         switch(var4) {
         case 0:
            mapping.remap(ModItems.PLOWCART);
            break;
         case 1:
            mapping.remap(ModItems.CARGOCART);
            break;
         case 2:
            mapping.remap(ModItems.WHEEL);
         }
      }

   }

   @SubscribeEvent
   public static void onMissingEntityMapping(MissingMappings<EntityEntry> event) {
      UnmodifiableIterator var1 = event.getAllMappings().iterator();

      while(var1.hasNext()) {
         Mapping<EntityEntry> mapping = (Mapping)var1.next();
         String var3 = mapping.key.toString();
         byte var4 = -1;
         switch(var3.hashCode()) {
         case -1894862512:
            if (var3.equals("astikoor:cargocart")) {
               var4 = 1;
            }
            break;
         case -1103426800:
            if (var3.equals("astikoor:plowcart")) {
               var4 = 0;
            }
         }

         switch(var4) {
         case 0:
            mapping.remap(EntityRegistry.getEntry(EntityPlowCart.class));
            break;
         case 1:
            mapping.remap(EntityRegistry.getEntry(EntityCargoCart.class));
         }
      }

   }
}
