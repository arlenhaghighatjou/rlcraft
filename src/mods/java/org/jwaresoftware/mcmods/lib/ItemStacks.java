package org.jwaresoftware.mcmods.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.RandomStringUtils;

public final class ItemStacks {
   private static final int WILDCARD = 32767;

   @Nonnull
   public static final ItemStack NULLSTACK() {
      return ItemStack.EMPTY;
   }

   @Nonnull
   public static final ItemStack safe(@Nullable ItemStack stack) {
      return stack == null ? NULLSTACK() : stack;
   }

   public static final int getSize(@Nullable ItemStack stack) {
      return stack != null ? stack.getCount() : 0;
   }

   public static final boolean isEmpty(@Nullable ItemStack stack) {
      return stack == null || stack.isEmpty();
   }

   public static final boolean isSingle(@Nullable ItemStack stack) {
      return stack != null ? stack.getCount() == 1 : false;
   }

   public static final void setSingle(@Nonnull ItemStack stack) {
      if (stack != ItemStack.EMPTY) {
         stack.setCount(1);
      }

   }

   public static final boolean setSize(@Nullable ItemStack stack, int size) {
      boolean is = true;
      if (stack != null && stack != ItemStack.EMPTY) {
         stack.setCount(size);
         is = stack.isEmpty();
      }

      return is;
   }

   public static final boolean decrBy(@Nullable ItemStack stack, int by) {
      boolean is = true;
      if (stack != null && stack != ItemStack.EMPTY) {
         stack.shrink(by);
         is = stack.isEmpty();
      }

      return is;
   }

   public static final void incrBy(@Nullable ItemStack stack, int by) {
      if (stack != null && stack != ItemStack.EMPTY) {
         stack.grow(by);
      }

   }

   public static final boolean areItemStacksEqual(@Nullable ItemStack stackA, @Nullable ItemStack stackB) {
      return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? ItemStack.areItemStacksEqual(stackA, stackB) : false);
   }

   public static final boolean areEqualWildcardIgnoreAmountsAndTags(ItemStack a, ItemStack b) {
      return !isEmpty(a) && !isEmpty(b) ? (a.getItem() == b.getItem() ? a.getMetadata() == 32767 || b.getMetadata() == 32767 : false) : false;
   }

   public static final boolean areEqualIgnoreAmountsAndTags(ItemStack a, ItemStack b) {
      return ItemStack.areItemsEqual(a, b) ? true : areEqualWildcardIgnoreAmountsAndTags(a, b);
   }

   @Nullable
   public static ItemStack copyItemStack(@Nullable ItemStack stack) {
      return stack == null ? null : (stack == ItemStack.EMPTY ? ItemStack.EMPTY : stack.copy());
   }

   @Nullable
   public static ItemStack copyItemStackSingle(@Nullable ItemStack stack) {
      if (stack == null) {
         return null;
      } else if (stack == ItemStack.EMPTY) {
         return ItemStack.EMPTY;
      } else {
         ItemStack copy = stack.copy();
         copy.setCount(1);
         return copy;
      }
   }

   @Nullable
   public static ItemStack copyItemStack(@Nonnull ItemStack stack, int n) {
      if (stack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack copy = stack.copy();
         copy.setCount(Math.max(0, n));
         return copy;
      }
   }

   @Nonnull
   public static ItemStack copyDisplayName(@Nonnull ItemStack from, @Nonnull ItemStack to) {
      if (from != to && from.hasTagCompound()) {
         NBTTagCompound displayData = from.getSubCompound("display");
         if (displayData != null) {
            if (displayData.hasKey("Name", 8)) {
               to.setStackDisplayName(displayData.getString("Name"));
            }

            if (displayData.hasKey("LocName", 8)) {
               to.setTranslatableName(displayData.getString("LocName"));
            }
         }
      }

      return to;
   }

   @Nonnull
   public static ItemStack copyWithUnlocalizedName(@Nonnull ItemStack stack, String key) {
      stack.setTranslatableName(key);
      return stack;
   }

   @Nonnull
   public static final NBTTagCompound getTagCompoundNonNull(@Nonnull ItemStack stack) {
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      return stack.getTagCompound();
   }

   public static final NBTTagCompound getTagCompoundNonNull(@Nonnull ItemStack stack, String key) {
      return stack.getOrCreateSubCompound(key);
   }

   public static final void setStackUnlocalizedName(ItemStack stack, String key) {
      if (!isEmpty(stack)) {
         stack.setTranslatableName(key);
      }

   }

   public static final void stampUnique(@Nonnull NBTTagCompound subcompound, String prefix, String chaosString) {
      if (prefix == null) {
         prefix = "";
      }

      if (chaosString == null) {
         chaosString = prefix + System.nanoTime() + RandomStringUtils.randomAlphanumeric(6);
      }

      subcompound.setString("CHAOS", chaosString);
   }

   public static final void stampUnique(@Nonnull ItemStack stack, String prefix) {
      stampUnique(getTagCompoundNonNull(stack), prefix, (String)null);
   }

   @Nonnull
   public static final IBlockState toBlock(@Nullable ItemStack sourcestack, @Nullable Block sourceblock) {
      IBlockState air = Blocks.AIR.getDefaultState();
      if (isEmpty(sourcestack)) {
         return air;
      } else {
         if (sourceblock == null) {
            sourceblock = Block.getBlockFromItem(sourcestack.getItem());
         }

         return sourceblock == Blocks.AIR ? air : sourceblock.getStateFromMeta(sourcestack.getMetadata());
      }
   }

   @Nonnull
   public static final IBlockState toBlock(@Nullable ItemStack sourcestack) {
      return toBlock(sourcestack, (Block)null);
   }

   @Nonnull
   public static final ItemStack fromBlock(@Nullable IBlockState sourceblock) {
      if (sourceblock != null && sourceblock.getBlock() != Blocks.AIR) {
         int meta = sourceblock.getBlock().getMetaFromState(sourceblock);
         return new ItemStack(sourceblock.getBlock(), 1, meta);
      } else {
         return NULLSTACK();
      }
   }

   public static final float getDurabilityLeft(@Nonnull ItemStack stack) {
      return isEmpty(stack) ? 0.0F : 1.0F - (float)stack.getItemDamage() / (float)stack.getMaxDamage();
   }

   public static final float getDurabilityUsed(@Nonnull ItemStack stack) {
      return isEmpty(stack) ? 1.0F : (float)stack.getItemDamage() / (float)stack.getMaxDamage();
   }

   public static final boolean isFlaggedUnbreakable(@Nonnull ItemStack stack) {
      return stack.hasTagCompound() && stack.getTagCompound().getBoolean("Unbreakable");
   }

   public static final boolean isFlaggedVanishing(@Nonnull ItemStack stack) {
      return stack.hasTagCompound() && stack.getTagCompound().getBoolean("Vanishing");
   }

   private ItemStacks() {
   }

   public static final void init() {
   }
}
