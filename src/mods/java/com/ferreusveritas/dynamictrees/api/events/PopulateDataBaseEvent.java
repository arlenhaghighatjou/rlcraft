package com.ferreusveritas.dynamictrees.api.events;

import com.ferreusveritas.dynamictrees.api.worldgen.IBiomeDataBasePopulator;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBase;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PopulateDataBaseEvent extends Event {
   private final BiomeDataBase biomeDataBase;
   private final IBiomeDataBasePopulator defaultPopulator;

   public PopulateDataBaseEvent(BiomeDataBase biomeDataBase, IBiomeDataBasePopulator defaultPopulator) {
      this.biomeDataBase = biomeDataBase;
      this.defaultPopulator = defaultPopulator;
   }

   public BiomeDataBase getBiomeDataBase() {
      return this.biomeDataBase;
   }

   public IBiomeDataBasePopulator getDefaultPopulator() {
      return this.defaultPopulator;
   }
}
