package com.fantasticsource.tiamatinventory.api;

import java.util.ArrayList;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface ITiamatPlayerInventory extends IInventory {
   ItemStack getSheathedMainhand1();

   void setSheathedMainhand1(ItemStack var1);

   ItemStack getSheathedOffhand1();

   void setSheathedOffhand1(ItemStack var1);

   ItemStack getSheathedMainhand2();

   void setSheathedMainhand2(ItemStack var1);

   ItemStack getSheathedOffhand2();

   void setSheathedOffhand2(ItemStack var1);

   ArrayList<ItemStack> getTiamatArmor();

   ItemStack getShoulders();

   void setShoulders(ItemStack var1);

   ItemStack getCape();

   void setCape(ItemStack var1);

   ArrayList<ItemStack> getQuickSlots();

   void setQuickSlot(int var1, ItemStack var2);

   ItemStack getBackpack();

   void setBackpack(ItemStack var1);

   ItemStack getPet();

   void setPet(ItemStack var1);

   ItemStack getDeck();

   void setDeck(ItemStack var1);

   ArrayList<ItemStack> getPlayerClasses();

   void setPlayerClass(int var1, ItemStack var2);

   ArrayList<ItemStack> getOffensiveSkills();

   void setOffensiveSkill(int var1, ItemStack var2);

   ArrayList<ItemStack> getUtilitySkills();

   void setUtilitySkill(int var1, ItemStack var2);

   ItemStack getUltimateSkill();

   void setUltimateSkill(ItemStack var1);

   ArrayList<ItemStack> getPassiveSkills();

   void setPassiveSkill(int var1, ItemStack var2);

   ArrayList<ItemStack> getGatheringProfessions();

   void setGatheringProfession(int var1, ItemStack var2);

   ArrayList<ItemStack> getCraftingProfessions();

   void setCraftingProfession(int var1, ItemStack var2);

   ArrayList<ItemStack> getCraftingRecipes();

   void setCraftingRecipe(int var1, ItemStack var2);

   ArrayList<ItemStack> getAllItems();

   ArrayList<ItemStack> getAllEquippedItems();
}
