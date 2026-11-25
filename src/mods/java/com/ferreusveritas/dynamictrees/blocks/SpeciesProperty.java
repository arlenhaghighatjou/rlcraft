package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.trees.Species;
import net.minecraftforge.common.property.IUnlistedProperty;

public class SpeciesProperty implements IUnlistedProperty<Species> {
   public static final SpeciesProperty SPECIES = new SpeciesProperty("species");
   private final String name;

   public SpeciesProperty(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public boolean isValid(Species value) {
      return value != null;
   }

   public Class<Species> getType() {
      return Species.class;
   }

   public String valueToString(Species value) {
      return value.toString();
   }
}
