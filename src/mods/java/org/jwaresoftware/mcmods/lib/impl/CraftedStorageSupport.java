package org.jwaresoftware.mcmods.lib.impl;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.jwaresoftware.mcmods.lib.ItemStacks;
import org.jwaresoftware.mcmods.lib.SharedGlue;
import org.jwaresoftware.mcmods.lib.api.ICraftedStorage;

public final class CraftedStorageSupport {
   private CraftedStorageSupport() {
   }

   public static class ForBlock implements ICraftedStorage {
      private final int _size;

      public ForBlock(int size) {
         this._size = size;
      }

      public ForBlock() {
         this(SharedGlue.TABLE_CRAFT_GRID_SIZE());
      }

      public ICraftedStorage.Type storedType() {
         return ICraftedStorage.Type.ITEM;
      }

      public int quantityStored(ItemStack stack, EntityPlayer unused) {
         return this._size * (stack != null ? ItemStacks.getSize(stack) : 1);
      }

      public int quantityStored(IBlockState block, EntityPlayer unused) {
         return this._size;
      }
   }

   public static class ForItem implements ICraftedStorage {
      private final int _size;

      public ForItem(int size) {
         this._size = size;
      }

      public ForItem() {
         this(SharedGlue.PLAYER_CRAFT_GRID_SIZE());
      }

      public ICraftedStorage.Type storedType() {
         return ICraftedStorage.Type.ITEM;
      }

      public int quantityStored(ItemStack stack, EntityPlayer unused) {
         return this._size * (stack != null ? ItemStacks.getSize(stack) : 1);
      }

      public int quantityStored(IBlockState block, EntityPlayer unused) {
         return -1;
      }
   }
}
