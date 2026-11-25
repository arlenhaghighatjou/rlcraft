package com.ferreusveritas.dynamictrees.systems.dropcreators;

import com.ferreusveritas.dynamictrees.trees.Species;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DropCreatorHarvest extends DropCreator {
   ItemStack droppedItem;
   float rate;

   public DropCreatorHarvest(ResourceLocation name, ItemStack droppedItem, float rate) {
      super(name);
      this.droppedItem = droppedItem;
      this.rate = rate;
   }

   public List<ItemStack> getHarvestDrop(World world, Species species, BlockPos leafPos, EaglercraftRandom random, List<ItemStack> dropList, int soilLife, int fortune) {
      if (this.rate > random.nextFloat()) {
         dropList.add(this.droppedItem.copy());
      }

      return dropList;
   }
}
