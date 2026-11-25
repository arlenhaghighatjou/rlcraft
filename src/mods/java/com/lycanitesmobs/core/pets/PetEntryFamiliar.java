package com.lycanitesmobs.core.pets;

import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;

public class PetEntryFamiliar extends PetEntry {
   public PetEntryFamiliar(UUID petEntryID, EntityLivingBase host, String summonType) {
      super(petEntryID, "familiar", host, summonType);
   }
}
