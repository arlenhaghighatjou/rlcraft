package com.ferreusveritas.dynamictrees.event;

import com.ferreusveritas.dynamictrees.trees.Species;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class SeedVoluntaryPlantEvent extends Event {
   protected EntityItem seedEntityItem;
   protected ItemStack seedStack;
   protected Species species;
   protected BlockPos pos;
   protected boolean willPlant = false;

   public SeedVoluntaryPlantEvent(EntityItem entityItem, Species species, BlockPos pos, boolean willPlant) {
      this.seedEntityItem = entityItem;
      this.pos = pos;
      this.species = species;
      this.willPlant = willPlant;
   }

   public Species getSpecies() {
      return this.species;
   }

   public void setSpecies(Species species) {
      this.species = species;
   }

   public EntityItem getEntityItem() {
      return this.seedEntityItem;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public void setWillPlant(boolean willPlant) {
      this.willPlant = willPlant;
   }

   public boolean getWillPlant() {
      return this.willPlant;
   }
}
