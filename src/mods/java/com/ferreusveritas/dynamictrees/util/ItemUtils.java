package com.ferreusveritas.dynamictrees.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class ItemUtils {
   public ItemUtils() {
   }

   public static void spawnItemStack(World world, BlockPos pos, ItemStack stack) {
      spawnItemStack(world, pos, stack, false);
   }

   public static void spawnItemStack(World world, BlockPos pos, ItemStack stack, boolean searchForAir) {
      if (searchForAir) {
         while(!world.isAirBlock(pos)) {
            pos = pos.up();
         }
      }

      EntityItem entityItem = new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, stack);
      entityItem.motionX = entityItem.motionY = entityItem.motionZ = 0.0D;
      world.spawnEntity(entityItem);
   }
}
