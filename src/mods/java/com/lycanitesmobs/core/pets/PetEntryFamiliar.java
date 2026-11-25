package com.lycanitesmobs.core.pets;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.EntityLivingBase;

public class PetEntryFamiliar extends PetEntry {
   public PetEntryFamiliar(EaglercraftUUID petEntryID, EntityLivingBase host, String summonType) {
      super(petEntryID, "familiar", host, summonType);
   }
}
