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

public class VenomThrowingRecipes extends Impl<IRecipe> implements IRecipe {
   private static final Map<Item, Item> weaponMapVenom;

   public VenomThrowingRecipes() {
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      return this.validInput(inv) != null;
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      Integer[] slots = this.validInput(inv);
      return slots == null ? ItemStack.EMPTY : new ItemStack((Item)weaponMapVenom.get(inv.getStackInSlot(slots[1].intValue()).getItem()));
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
            if (itemStack.getItem() != ModItems.myrmex_stinger) {
               if (weaponMapVenom.containsKey(itemStack.getItem()) && !itemStack.isItemDamaged()) {
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
      Map<Item, Item> tempVenomMap = new HashMap();
      tempVenomMap.put(Item.getByNameOrId("spartanfire:throwing_knife_desert"), Item.getByNameOrId("spartanfire:throwing_knife_desert_venom"));
      tempVenomMap.put(Item.getByNameOrId("spartanfire:javelin_desert"), Item.getByNameOrId("spartanfire:javelin_desert_venom"));
      tempVenomMap.put(Item.getByNameOrId("spartanfire:boomerang_desert"), Item.getByNameOrId("spartanfire:boomerang_desert_venom"));
      tempVenomMap.put(Item.getByNameOrId("spartanfire:throwing_knife_jungle"), Item.getByNameOrId("spartanfire:throwing_knife_jungle_venom"));
      tempVenomMap.put(Item.getByNameOrId("spartanfire:javelin_jungle"), Item.getByNameOrId("spartanfire:javelin_jungle_venom"));
      tempVenomMap.put(Item.getByNameOrId("spartanfire:boomerang_jungle"), Item.getByNameOrId("spartanfire:boomerang_jungle_venom"));
      weaponMapVenom = Collections.unmodifiableMap(tempVenomMap);
   }
}
