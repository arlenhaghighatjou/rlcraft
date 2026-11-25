package com.ferreusveritas.dynamictrees.worldgen;

import com.ferreusveritas.dynamictrees.ModConfigs;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class TreeGenCancelEventHandler {
   public TreeGenCancelEventHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public void onEvent(Decorate event) {
      int dimensionId = event.getWorld().provider.getDimension();
      BiomeDataBase dbase = TreeGenerator.getTreeGenerator().getBiomeDataBase(dimensionId);
      if (dbase != TreeGenerator.DIMENSIONBLACKLISTED && !ModConfigs.dimensionBlacklist.contains(Integer.valueOf(dimensionId))) {
         Biome biome = event.getWorld().getBiome(event.getPos());
         switch(event.getType()) {
         case CACTUS:
            if (ModConfigs.vanillaCactusWorldGen) {
               break;
            }
         case BIG_SHROOM:
         case TREE:
            if (dbase.getEntry(biome).shouldCancelVanillaTreeGen()) {
               event.setResult(Result.DENY);
            }
         }
      }

   }
}
