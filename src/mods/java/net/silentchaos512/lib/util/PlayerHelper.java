package net.silentchaos512.lib.util;

import com.google.common.collect.ImmutableList;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class PlayerHelper {
   public PlayerHelper() {
   }

   public static void giveItem(EntityPlayer player, ItemStack stack) {
      ItemStack copy = stack.copy();
      if (!player.inventory.addItemStackToInventory(copy)) {
         EntityItem entityItem = new EntityItem(player.world, player.posX, player.posY + 1.0D, player.posZ, copy);
         entityItem.setNoPickupDelay();
         entityItem.setOwner(player.getName());
         player.world.spawnEntity(entityItem);
      }

   }

   public static void removeItem(EntityPlayer player, ItemStack stack) {
      for(NonNullList<ItemStack> inv : ImmutableList.of(player.inventory.mainInventory, player.inventory.offHandInventory, player.inventory.armorInventory)) {
         for(int i = 0; i < inv.size(); ++i) {
            if (stack == inv.get(i)) {
               inv.set(i, ItemStack.EMPTY);
               return;
            }
         }
      }

   }

   public static NBTTagCompound getPersistedDataSubcompound(EntityPlayer player, String subcompoundKey) {
      NBTTagCompound forgeData = player.getEntityData();
      if (!forgeData.hasKey("PlayerPersisted")) {
         forgeData.setTag("PlayerPersisted", new NBTTagCompound());
      }

      NBTTagCompound persistedData = forgeData.getCompoundTag("PlayerPersisted");
      if (!persistedData.hasKey(subcompoundKey)) {
         persistedData.setTag(subcompoundKey, new NBTTagCompound());
      }

      return persistedData.getCompoundTag(subcompoundKey);
   }

   @Nonnull
   public static NonNullList<ItemStack> getNonEmptyStacks(EntityPlayer player) {
      return getNonEmptyStacks(player, true, true, true, (s) -> {
         return true;
      });
   }

   @Nonnull
   public static NonNullList<ItemStack> getNonEmptyStacks(EntityPlayer player, Predicate<ItemStack> predicate) {
      return getNonEmptyStacks(player, true, true, true, predicate);
   }

   @Nonnull
   public static NonNullList<ItemStack> getNonEmptyStacks(EntityPlayer player, boolean includeMain, boolean includeOffHand, boolean includeArmor) {
      return getNonEmptyStacks(player, includeMain, includeOffHand, includeArmor, (s) -> {
         return true;
      });
   }

   @Nonnull
   public static NonNullList<ItemStack> getNonEmptyStacks(EntityPlayer player, boolean includeMain, boolean includeOffHand, boolean includeArmor, Predicate<ItemStack> predicate) {
      NonNullList<ItemStack> list = NonNullList.create();
      if (includeMain) {
         for(ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && predicate.test(stack)) {
               list.add(stack);
            }
         }
      }

      if (includeOffHand) {
         for(ItemStack stack : player.inventory.offHandInventory) {
            if (!stack.isEmpty() && predicate.test(stack)) {
               list.add(stack);
            }
         }
      }

      if (includeArmor) {
         for(ItemStack stack : player.inventory.armorInventory) {
            if (!stack.isEmpty() && predicate.test(stack)) {
               list.add(stack);
            }
         }
      }

      return list;
   }

   @Nonnull
   public static ItemStack getFirstValidStack(EntityPlayer player, boolean includeMain, boolean includeOffHand, boolean includeArmor, Predicate<ItemStack> predicate) {
      if (includeOffHand) {
         for(ItemStack stack : player.inventory.offHandInventory) {
            if (!stack.isEmpty() && predicate.test(stack)) {
               return stack;
            }
         }
      }

      if (includeArmor) {
         for(ItemStack stack : player.inventory.armorInventory) {
            if (!stack.isEmpty() && predicate.test(stack)) {
               return stack;
            }
         }
      }

      if (includeMain) {
         for(ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && predicate.test(stack)) {
               return stack;
            }
         }
      }

      return ItemStack.EMPTY;
   }
}
