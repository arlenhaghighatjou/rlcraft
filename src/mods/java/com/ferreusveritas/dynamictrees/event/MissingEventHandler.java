package com.ferreusveritas.dynamictrees.event;

import com.google.common.collect.UnmodifiableIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.fml.common.Mod.EventHandler;

public class MissingEventHandler {
   public MissingEventHandler() {
   }

   @EventHandler
   public void missingBlockMappings(MissingMappings<Block> event) {
      UnmodifiableIterator var2 = event.getMappings().iterator();

      while(var2.hasNext()) {
         Mapping<Block> missing = (Mapping)var2.next();
         ResourceLocation resLoc = missing.key;
         String domain = resLoc.getResourceDomain();
         String path = resLoc.getResourcePath();
         if (domain.equals("growingtrees")) {
            Logger.getLogger("dynamictrees").log(Level.CONFIG, "Remapping Missing Block: " + path);
            Block mappedBlock = (Block)Block.REGISTRY.getObject(new ResourceLocation("dynamictrees", path));
            if (mappedBlock != Blocks.AIR) {
               missing.remap(mappedBlock);
            }
         }
      }

   }

   @EventHandler
   public void missingItemMappings(MissingMappings<Item> event) {
      UnmodifiableIterator var2 = event.getMappings().iterator();

      while(var2.hasNext()) {
         Mapping<Item> missing = (Mapping)var2.next();
         ResourceLocation resLoc = missing.key;
         String domain = resLoc.getResourceDomain();
         String path = resLoc.getResourcePath();
         if (domain.equals("growingtrees")) {
            Logger.getLogger("dynamictrees").log(Level.CONFIG, "Remapping Missing Item: " + path);
            Item mappedItem = (Item)Item.REGISTRY.getObject(new ResourceLocation("dynamictrees", path));
            if (mappedItem != null) {
               missing.remap(mappedItem);
            }
         }
      }

   }
}
