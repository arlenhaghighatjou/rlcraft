package com.ferreusveritas.dynamictrees.systems.dropcreators;

import com.ferreusveritas.dynamictrees.trees.Species;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DropCreatorVoluntary extends DropCreator {
   ItemStack droppedItem;
   float rate;

   public DropCreatorVoluntary(ResourceLocation name, ItemStack droppedItem, float rate) {
      super(name);
      this.droppedItem = droppedItem;
      this.rate = rate;
   }

   public List<ItemStack> getVoluntaryDrop(World world, Species species, BlockPos rootPos, EaglercraftRandom random, List<ItemStack> dropList, int soilLife) {
      if (this.rate > random.nextFloat()) {
         dropList.add(this.droppedItem.copy());
      }

      return dropList;
   }
}
