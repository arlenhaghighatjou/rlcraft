package com.chaosbuffalo.spartanfire.recipes;

import com.github.alexthe666.iceandfire.core.ModItems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class FireIceThrowingRecipes extends Impl<IRecipe> implements IRecipe {
   private static final Map<Item, Item> weaponMapFire;
   private static final Map<Item, Item> weaponMapIce;

   public FireIceThrowingRecipes() {
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      return this.validInput(inv) != null;
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      Integer[] slots = this.validInput(inv);
      if (slots == null) {
         return ItemStack.EMPTY;
      } else {
         ItemStack stackInput = inv.getStackInSlot(slots[1].intValue());
         ItemStack output;
         if (inv.getStackInSlot(slots[0].intValue()).getItem() == ModItems.fire_dragon_blood) {
            output = new ItemStack((Item)weaponMapFire.get(stackInput.getItem()));
         } else {
            output = new ItemStack((Item)weaponMapIce.get(stackInput.getItem()));
         }

         return output;
      }
   }

   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }

   @Nullable
   private Integer[] validInput(InventoryCrafting inv) {
      int numStacks = 0;
      int bloodSlot = -1;
      int weaponSlot = -1;
      List<Integer> occupiedSlots = new ArrayList();

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         if (!inv.getStackInSlot(i).isEmpty()) {
            ++numStacks;
            occupiedSlots.add(Integer.valueOf(i));
         }
      }

      if (numStacks != 2) {
         return null;
      } else {
         Iterator var10 = occupiedSlots.iterator();

         while(var10.hasNext()) {
            int i = ((Integer)var10.next()).intValue();
            ItemStack itemStack = inv.getStackInSlot(i);
            if (itemStack.getItem() != ModItems.fire_dragon_blood && itemStack.getItem() != ModItems.ice_dragon_blood) {
               if (weaponMapFire.containsKey(itemStack.getItem()) && !itemStack.isItemDamaged()) {
                  NBTTagCompound compound = itemStack.getTagCompound();
                  if (compound != null && compound.getByte("Original") == 1) {
                     weaponSlot = i;
                     continue;
                  }

                  return null;
               }

               return null;
            } else {
               bloodSlot = i;
            }
         }

         Integer[] slots = new Integer[]{bloodSlot, weaponSlot};
         return bloodSlot != -1 && weaponSlot != -1 ? slots : null;
      }
   }

   static {
      Map<Item, Item> tempFireMap = new HashMap();
      Map<Item, Item> tempIceMap = new HashMap();
      tempFireMap.put(Item.getByNameOrId("spartanfire:throwing_axe_dragonbone"), Item.getByNameOrId("spartanfire:throwing_axe_fire_dragonbone"));
      tempFireMap.put(Item.getByNameOrId("spartanfire:throwing_knife_dragonbone"), Item.getByNameOrId("spartanfire:throwing_knife_fire_dragonbone"));
      tempFireMap.put(Item.getByNameOrId("spartanfire:javelin_dragonbone"), Item.getByNameOrId("spartanfire:javelin_fire_dragonbone"));
      tempFireMap.put(Item.getByNameOrId("spartanfire:boomerang_dragonbone"), Item.getByNameOrId("spartanfire:boomerang_fire_dragonbone"));
      tempIceMap.put(Item.getByNameOrId("spartanfire:throwing_axe_dragonbone"), Item.getByNameOrId("spartanfire:throwing_axe_ice_dragonbone"));
      tempIceMap.put(Item.getByNameOrId("spartanfire:throwing_knife_dragonbone"), Item.getByNameOrId("spartanfire:throwing_knife_ice_dragonbone"));
      tempIceMap.put(Item.getByNameOrId("spartanfire:javelin_dragonbone"), Item.getByNameOrId("spartanfire:javelin_ice_dragonbone"));
      tempIceMap.put(Item.getByNameOrId("spartanfire:boomerang_dragonbone"), Item.getByNameOrId("spartanfire:boomerang_ice_dragonbone"));
      weaponMapFire = Collections.unmodifiableMap(tempFireMap);
      weaponMapIce = Collections.unmodifiableMap(tempIceMap);
   }
}
