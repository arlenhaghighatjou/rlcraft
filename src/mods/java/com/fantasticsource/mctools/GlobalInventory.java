package com.fantasticsource.mctools;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.fantasticsource.fantasticaw.api.FantasticAWAPI;
import com.fantasticsource.fantasticlib.Compat;
import com.fantasticsource.tiamatinventory.api.ITiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.api.TiamatInventoryAPI;
import com.fantasticsource.tiamatitems.api.TiamatItemsAPI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.function.Predicate;
import moe.plushie.armourers_workshop.api.ArmourersWorkshopApi;
import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinTypeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class GlobalInventory {
   public static ISkinTypeRegistry skinTypeRegistry = ArmourersWorkshopApi.skinTypeRegistry;

   public GlobalInventory() {
   }

   public static ArrayList<ItemStack> getAllItems(Entity entity) {
      ArrayList<ItemStack> result = getAllNonSkinItems(entity);
      result.addAll(getAWSkins(entity));
      return result;
   }

   public static ArrayList<ItemStack> getAllNonSkinItems(Entity entity) {
      ArrayList<ItemStack> result = new ArrayList();
      result.addAll(getVanillaItems(entity));
      result.addAll(getBaubles(entity));
      ITiamatPlayerInventory inventory = getTiamatInventory(entity);
      if (inventory != null) {
         result.addAll(inventory.getAllItems());
      }

      return result;
   }

   public static ArrayList<ItemStack> getAllEquippedItems(Entity entity) {
      ArrayList<ItemStack> result = getAllEquippedNonSkinItems(entity);
      result.addAll(getAWSkins(entity));
      return result;
   }

   public static ArrayList<ItemStack> getAllEquippedNonSkinItems(Entity entity) {
      ArrayList<ItemStack> result = new ArrayList();
      ItemStack stack = getVanillaMainhandItem(entity);
      if (stack != null) {
         result.add(stack);
      }

      result.addAll(getVanillaOffhandItems(entity));
      result.addAll(getVanillaArmorItems(entity));
      result.addAll(getBaubles(entity));
      ITiamatPlayerInventory tiamatInventory = getTiamatInventory(entity);
      if (tiamatInventory != null) {
         result.addAll(tiamatInventory.getAllEquippedItems());
      }

      return result;
   }

   public static LinkedHashMap<String, ArrayList<ItemStack>> getAllItemsCategorized(Entity entity) {
      LinkedHashMap<String, ArrayList<ItemStack>> result = new LinkedHashMap();
      ArrayList<ItemStack> list = new ArrayList();
      ItemStack mainhand = getVanillaMainhandItem(entity);
      if (mainhand != null) {
         list.add(mainhand);
         result.put("Vanilla Mainhand", list);
         list = new ArrayList();
      }

      list.addAll(getVanillaOffhandItems(entity));
      if (list.size() > 0) {
         result.put("Vanilla Offhands", list);
         list = new ArrayList();
      }

      list.addAll(getVanillaHotbarItems(entity, false, true));
      if (mainhand != null) {
         list.remove(mainhand);
      }

      if (list.size() > 0) {
         result.put("Vanilla Hotbar (Minus Mainhand)", list);
         list = new ArrayList();
      }

      list.addAll(getVanillaArmorItems(entity));
      if (list.size() > 0) {
         result.put("Vanilla Armor", list);
         list = new ArrayList();
      }

      list.addAll(getVanillaCargoItems(entity));
      if (list.size() > 0) {
         result.put("Vanilla Cargo", list);
         list = new ArrayList();
      }

      list.addAll(getBaublesAmulets(entity));
      if (list.size() > 0) {
         result.put("Baubles Amulets", list);
         list = new ArrayList();
      }

      list.addAll(getBaublesRings(entity));
      if (list.size() > 0) {
         result.put("Baubles Rings", list);
         list = new ArrayList();
      }

      list.addAll(getBaublesBelts(entity));
      if (list.size() > 0) {
         result.put("Baubles Belts", list);
         list = new ArrayList();
      }

      list.addAll(getBaublesHeadItems(entity));
      if (list.size() > 0) {
         result.put("Baubles Head Items", list);
         list = new ArrayList();
      }

      list.addAll(getBaublesBodyItems(entity));
      if (list.size() > 0) {
         result.put("Baubles Body Items", list);
         list = new ArrayList();
      }

      list.addAll(getBaublesCharms(entity));
      if (list.size() > 0) {
         result.put("Baubles Charms", list);
         list = new ArrayList();
      }

      ITiamatPlayerInventory inventory = getTiamatInventory(entity);
      if (inventory != null) {
         ItemStack stack = getTiamatSheathedMainhand1(inventory);
         if (stack != null) {
            list.add(stack);
            result.put("Tiamat Sheathed Mainhand 1", list);
            list = new ArrayList();
         }

         stack = getTiamatSheathedOffhand1(inventory);
         if (stack != null) {
            list.add(stack);
            result.put("Tiamat Sheathed Offhand 1", list);
            list = new ArrayList();
         }

         stack = getTiamatSheathedMainhand2(inventory);
         if (stack != null) {
            list.add(stack);
            result.put("Tiamat Sheathed Mainhand 2", list);
            list = new ArrayList();
         }

         stack = getTiamatSheathedOffhand2(inventory);
         if (stack != null) {
            list.add(stack);
            result.put("Tiamat Sheathed Offhand 2", list);
            list = new ArrayList();
         }

         list.addAll(getTiamatArmor(inventory));
         if (list.size() > 0) {
            result.put("Tiamat Armor", list);
            list = new ArrayList();
         }

         list.addAll(getTiamatQuickslots(inventory));
         if (list.size() > 0) {
            result.put("Tiamat Quick Items", list);
            list = new ArrayList();
         }

         stack = getTiamatBackpack(inventory);
         if (stack != null) {
            list.add(stack);
            result.put("Tiamat Backpack", list);
            list = new ArrayList();
         }

         stack = getTiamatPet(inventory);
         if (stack != null) {
            list.add(stack);
            result.put("Tiamat Pet", list);
            list = new ArrayList();
         }

         stack = getTiamatDeck(inventory);
         if (stack != null) {
            list.add(stack);
            result.put("Tiamat Deck", list);
            list = new ArrayList();
         }

         list.addAll(getTiamatClasses(inventory));
         if (list.size() > 0) {
            result.put("Tiamat Classes", list);
            list = new ArrayList();
         }

         list.addAll(getTiamatOffensiveSkills(inventory));
         if (list.size() > 0) {
            result.put("Tiamat Offensive Skills", list);
            list = new ArrayList();
         }

         list.addAll(getTiamatUtilitySkills(inventory));
         if (list.size() > 0) {
            result.put("Tiamat Utility Skills", list);
            list = new ArrayList();
         }

         stack = getTiamatUltimateSkill(inventory);
         if (stack != null) {
            list.add(stack);
            result.put("Tiamat Ultimate Skill", list);
            list = new ArrayList();
         }

         list.addAll(getTiamatPassiveSkills(inventory));
         if (list.size() > 0) {
            result.put("Tiamat Passive Skills", list);
            list = new ArrayList();
         }

         list.addAll(getTiamatGatheringProfessions(inventory));
         if (list.size() > 0) {
            result.put("Tiamat Gathering Professions", list);
            list = new ArrayList();
         }

         list.addAll(getTiamatCraftingProfessions(inventory));
         if (list.size() > 0) {
            result.put("Tiamat Crafting Professions", list);
            list = new ArrayList();
         }

         list.addAll(getTiamatRecipes(inventory));
         if (list.size() > 0) {
            result.put("Tiamat Recipes", list);
            list = new ArrayList();
         }
      }

      list.addAll(getAWSkins(entity));
      if (list.size() > 0) {
         result.put("Armourer's Workshop Skins", list);
         new ArrayList();
      }

      return result;
   }

   public static ArrayList<ItemStack> getValidEquippedItems(Entity entity) {
      ArrayList<ItemStack> result = new ArrayList();
      ArrayList<ItemStack> offhands = getVanillaOffhandItems(entity);
      int emptyHandsAvailable = 0;

      for(ItemStack stack : offhands) {
         if (stack.isEmpty()) {
            ++emptyHandsAvailable;
         }
      }

      ItemStack mainhand = getVanillaMainhandItem(entity);
      if (mainhand != null) {
         if (mainhand.isEmpty()) {
            ++emptyHandsAvailable;
         } else {
            String slotting = Slottings.getItemSlotting(mainhand);
            if (!slotting.equals("Mainhand") && !slotting.equals("Hand") && !slotting.equals("Hotbar") && !slotting.equals("Any")) {
               if (slotting.equals("Tiamat 2H") && (emptyHandsAvailable > 0 || !Compat.tiamatinventory)) {
                  result.add(mainhand);
                  --emptyHandsAvailable;
               }
            } else {
               result.add(mainhand);
            }
         }
      }

      for(ItemStack stack : offhands) {
         String slotting = Slottings.getItemSlotting(stack);
         if (!slotting.equals("Offhand") && !slotting.equals("Hand") && !slotting.equals("Any")) {
            if (slotting.equals("Tiamat 2H") && (emptyHandsAvailable > 0 || !Compat.tiamatinventory)) {
               result.add(stack);
               --emptyHandsAvailable;
            }
         } else {
            result.add(stack);
         }
      }

      for(ItemStack stack : getVanillaHotbarItems(entity, false, true)) {
         String slotting = Slottings.getItemSlotting(stack);
         if (slotting.equals("Hotbar") || slotting.equals("Any")) {
            result.add(stack);
         }
      }

      ItemStack stack2 = getVanillaHeadItem(entity);
      if (stack2 != null) {
         String slotting = Slottings.getItemSlotting(stack2);
         if (slotting.equals("Head") || slotting.equals("Armor") || slotting.equals("Any")) {
            result.add(stack2);
         }
      }

      stack2 = getVanillaChestItem(entity);
      if (stack2 != null) {
         String slotting = Slottings.getItemSlotting(stack2);
         if (slotting.equals("Chest") || slotting.equals("Armor") || slotting.equals("Any")) {
            result.add(stack2);
         }
      }

      stack2 = getVanillaLegItem(entity);
      if (stack2 != null) {
         String slotting = Slottings.getItemSlotting(stack2);
         if (slotting.equals("Legs") || slotting.equals("Armor") || slotting.equals("Any")) {
            result.add(stack2);
         }
      }

      stack2 = getVanillaFootItem(entity);
      if (stack2 != null) {
         String slotting = Slottings.getItemSlotting(stack2);
         if (slotting.equals("Feet") || slotting.equals("Armor") || slotting.equals("Any")) {
            result.add(stack2);
         }
      }

      for(ItemStack stack : getVanillaCargoItems(entity)) {
         String slotting = Slottings.getItemSlotting(stack);
         if (slotting.equals("Cargo") || slotting.equals("Any")) {
            result.add(stack);
         }
      }

      if (Compat.baubles) {
         for(ItemStack stack : getBaublesAmulets(entity)) {
            String slotting = Slottings.getItemSlotting(stack);
            if (slotting.equals("Baubles Amulet") || slotting.equals("Baubles Trinket") || slotting.equals("Any")) {
               result.add(stack);
            }
         }

         for(ItemStack stack : getBaublesRings(entity)) {
            String slotting = Slottings.getItemSlotting(stack);
            if (slotting.equals("Baubles Ring") || slotting.equals("Baubles Trinket") || slotting.equals("Any")) {
               result.add(stack);
            }
         }

         for(ItemStack stack : getBaublesBelts(entity)) {
            String slotting = Slottings.getItemSlotting(stack);
            if (slotting.equals("Baubles Belt") || slotting.equals("Baubles Trinket") || slotting.equals("Any")) {
               result.add(stack);
            }
         }

         for(ItemStack stack : getBaublesHeadItems(entity)) {
            String slotting = Slottings.getItemSlotting(stack);
            if (slotting.equals("Baubles Head") || slotting.equals("Baubles Trinket") || slotting.equals("Any")) {
               result.add(stack);
            }
         }

         for(ItemStack stack : getBaublesBodyItems(entity)) {
            String slotting = Slottings.getItemSlotting(stack);
            if (slotting.equals("Baubles Body") || slotting.equals("Baubles Trinket") || slotting.equals("Any")) {
               result.add(stack);
            }
         }

         for(ItemStack stack : getBaublesCharms(entity)) {
            String slotting = Slottings.getItemSlotting(stack);
            if (slotting.equals("Baubles Charm") || slotting.equals("Baubles Trinket") || slotting.equals("Any")) {
               result.add(stack);
            }
         }
      }

      if (Compat.tiamatinventory) {
         stack2 = getTiamatShoulderItem(entity);
         if (stack2 != null) {
            String slotting = Slottings.getItemSlotting(stack2);
            if (slotting.equals("Tiamat Shoulders") || slotting.equals("Armor") || slotting.equals("Any")) {
               result.add(stack2);
            }
         }

         stack2 = getTiamatCapeItem(entity);
         if (stack2 != null) {
            String slotting = Slottings.getItemSlotting(stack2);
            if (slotting.equals("Tiamat Cape") || slotting.equals("Armor") || slotting.equals("Any")) {
               result.add(stack2);
            }
         }

         for(ItemStack stack : getTiamatQuickslots(entity)) {
            String slotting = Slottings.getItemSlotting(stack);
            if (slotting.equals("Tiamat Quick Item") || slotting.equals("Any")) {
               result.add(stack);
            }
         }

         stack2 = getTiamatBackpack(entity);
         if (stack2 != null) {
            String slotting = Slottings.getItemSlotting(stack2);
            if (slotting.equals("Tiamat Backpack") || slotting.equals("Any")) {
               result.add(stack2);
            }
         }

         stack2 = getTiamatPet(entity);
         if (stack2 != null) {
            String slotting = Slottings.getItemSlotting(stack2);
            if (slotting.equals("Tiamat Pet") || slotting.equals("Any")) {
               result.add(stack2);
            }
         }

         stack2 = getTiamatDeck(entity);
         if (stack2 != null) {
            String slotting = Slottings.getItemSlotting(stack2);
            if (slotting.equals("Tiamat Deck") || slotting.equals("Any")) {
               result.add(stack2);
            }
         }

         for(ItemStack stack : getTiamatClasses(entity)) {
            String slotting = Slottings.getItemSlotting(stack);
            if (slotting.equals("Tiamat Class") || slotting.equals("Any")) {
               result.add(stack);
            }
         }

         for(ItemStack stack : getTiamatOffensiveSkills(entity)) {
            String slotting = Slottings.getItemSlotting(stack);
            if (slotting.equals("Tiamat Offensive Skill") || slotting.equals("Tiamat Skill") || slotting.equals("Any")) {
               result.add(stack);
            }
         }

         for(ItemStack stack : getTiamatUtilitySkills(entity)) {
            String slotting = Slottings.getItemSlotting(stack);
            if (slotting.equals("Tiamat Utility Skill") || slotting.equals("Tiamat Skill") || slotting.equals("Any")) {
               result.add(stack);
            }
         }

         stack2 = getTiamatUltimateSkill(entity);
         if (stack2 != null) {
            String slotting = Slottings.getItemSlotting(stack2);
            if (slotting.equals("Tiamat Ultimate Skill") || slotting.equals("Tiamat Skill") || slotting.equals("Any")) {
               result.add(stack2);
            }
         }

         for(ItemStack stack : getTiamatPassiveSkills(entity)) {
            String slotting = Slottings.getItemSlotting(stack);
            if (slotting.equals("Tiamat Passive Skill") || slotting.equals("Tiamat Skill") || slotting.equals("Any")) {
               result.add(stack);
            }
         }

         for(ItemStack stack : getTiamatGatheringProfessions(entity)) {
            String slotting = Slottings.getItemSlotting(stack);
            if (slotting.equals("Tiamat Gathering Profession") || slotting.equals("Tiamat Profession") || slotting.equals("Any")) {
               result.add(stack);
            }
         }

         for(ItemStack stack : getTiamatCraftingProfessions(entity)) {
            String slotting = Slottings.getItemSlotting(stack);
            if (slotting.equals("Tiamat Crafting Profession") || slotting.equals("Tiamat Profession") || slotting.equals("Any")) {
               result.add(stack);
            }
         }

         for(ItemStack stack : getTiamatRecipes(entity)) {
            String slotting = Slottings.getItemSlotting(stack);
            if (slotting.equals("Tiamat Recipe") || slotting.equals("Any")) {
               result.add(stack);
            }
         }
      }

      result.removeIf((stackx) -> {
         return !TiamatItemsAPI.isUsable(stack);
      });
      return result;
   }

   public static ArrayList<ItemStack> getVanillaItems(Entity entity) {
      ArrayList<ItemStack> result = new ArrayList();
      ItemStack stack = getVanillaMainhandItem(entity);
      if (stack != null) {
         result.add(stack);
      }

      result.addAll(getVanillaOffhandItems(entity));
      result.addAll(getVanillaHotbarItems(entity, false, true));
      result.addAll(getVanillaArmorItems(entity));
      result.addAll(getVanillaCargoItems(entity));
      return result;
   }

   public static ItemStack getVanillaMainhandItem(Entity entity) {
      if (entity instanceof EntityLivingBase) {
         return ((EntityLivingBase)entity).getHeldItemMainhand();
      } else {
         Iterator var1 = entity.getHeldEquipment().iterator();
         if (var1.hasNext()) {
            ItemStack stack = (ItemStack)var1.next();
            return stack;
         } else {
            return null;
         }
      }
   }

   public static void setVanillaMainhandItem(Entity entity, ItemStack stack) {
      entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
   }

   public static ArrayList<ItemStack> getVanillaOffhandItems(Entity entity) {
      ArrayList<ItemStack> result = new ArrayList();
      ItemStack mainhand = getVanillaMainhandItem(entity);

      for(ItemStack stack : entity.getHeldEquipment()) {
         if (stack.equals(mainhand)) {
            mainhand = null;
         } else {
            result.add(stack);
         }
      }

      return result;
   }

   public static void setVanillaOffhandItem(Entity entity, ItemStack stack) {
      entity.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, stack);
   }

   public static ArrayList<ItemStack> getVanillaHotbarItems(Entity entity, boolean includeMainhand, boolean orderFromMainhand) {
      ArrayList<ItemStack> result = new ArrayList();
      if (entity instanceof EntityPlayer) {
         InventoryPlayer inv = ((EntityPlayer)entity).inventory;
         if (orderFromMainhand) {
            for(int i = includeMainhand ? 0 : 1; i < 9; ++i) {
               result.add(inv.getStackInSlot((inv.currentItem + i) % 9));
            }
         } else {
            for(int i = 0; i < 9; ++i) {
               if (includeMainhand || i != inv.currentItem) {
                  result.add(inv.getStackInSlot(i));
               }
            }
         }
      } else if (includeMainhand) {
         ItemStack stack = getVanillaMainhandItem(entity);
         if (stack != null) {
            result.add(stack);
         }
      }

      return result;
   }

   public static ArrayList<ItemStack> getVanillaArmorItems(Entity entity) {
      ArrayList<ItemStack> result = new ArrayList();

      for(ItemStack stack : entity.getArmorInventoryList()) {
         result.add(stack);
      }

      return result;
   }

   public static ItemStack getVanillaHeadItem(Entity entity) {
      ArrayList<ItemStack> armor = getVanillaArmorItems(entity);
      return armor != null && armor.size() == 4 ? (ItemStack)armor.get(EntityEquipmentSlot.HEAD.getIndex()) : null;
   }

   public static void setVanillaHeadItem(Entity entity, ItemStack stack) {
      entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, stack);
   }

   public static ItemStack getVanillaChestItem(Entity entity) {
      ArrayList<ItemStack> armor = getVanillaArmorItems(entity);
      return armor != null && armor.size() == 4 ? (ItemStack)armor.get(EntityEquipmentSlot.CHEST.getIndex()) : null;
   }

   public static void setVanillaChestItem(Entity entity, ItemStack stack) {
      entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
   }

   public static ItemStack getVanillaLegItem(Entity entity) {
      ArrayList<ItemStack> armor = getVanillaArmorItems(entity);
      return armor != null && armor.size() == 4 ? (ItemStack)armor.get(EntityEquipmentSlot.LEGS.getIndex()) : null;
   }

   public static void setVanillaLegItem(Entity entity, ItemStack stack) {
      entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, stack);
   }

   public static ItemStack getVanillaFootItem(Entity entity) {
      ArrayList<ItemStack> armor = getVanillaArmorItems(entity);
      return armor != null && armor.size() == 4 ? (ItemStack)armor.get(EntityEquipmentSlot.FEET.getIndex()) : null;
   }

   public static void setVanillaFootItem(Entity entity, ItemStack stack) {
      entity.setItemStackToSlot(EntityEquipmentSlot.FEET, stack);
   }

   public static ArrayList<ItemStack> getVanillaCargoItems(Entity entity) {
      ArrayList<ItemStack> result = new ArrayList();
      if (!(entity instanceof EntityPlayer)) {
         return result;
      } else {
         ArrayList<ItemStack> ignore = new ArrayList();
         ignore.addAll(getVanillaHotbarItems(entity, true, false));
         ignore.addAll(getVanillaOffhandItems(entity));
         ignore.addAll(getVanillaArmorItems(entity));
         InventoryPlayer inventory = ((EntityPlayer)entity).inventory;

         for(int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!ignore.remove(stack)) {
               result.add(stack);
            }
         }

         return result;
      }
   }

   public static ArrayList<ItemStack> getBaubles(Entity entity) {
      ArrayList<ItemStack> result = new ArrayList();
      if (Compat.baubles && entity instanceof EntityPlayer) {
         IBaublesItemHandler inventory = BaublesApi.getBaublesHandler((EntityPlayer)entity);

         for(int i = 0; i < inventory.getSlots(); ++i) {
            result.add(inventory.getStackInSlot(i));
         }

         return result;
      } else {
         return result;
      }
   }

   public static ArrayList<ItemStack> getBaublesAmulets(Entity entity) {
      ArrayList<ItemStack> result = new ArrayList();
      if (Compat.baubles && entity instanceof EntityPlayer) {
         IBaublesItemHandler inventory = BaublesApi.getBaublesHandler((EntityPlayer)entity);

         for(int i : BaubleType.AMULET.getValidSlots()) {
            result.add(inventory.getStackInSlot(i));
         }

         return result;
      } else {
         return result;
      }
   }

   public static ArrayList<ItemStack> getBaublesRings(Entity entity) {
      ArrayList<ItemStack> result = new ArrayList();
      if (Compat.baubles && entity instanceof EntityPlayer) {
         IBaublesItemHandler inventory = BaublesApi.getBaublesHandler((EntityPlayer)entity);

         for(int i : BaubleType.RING.getValidSlots()) {
            result.add(inventory.getStackInSlot(i));
         }

         return result;
      } else {
         return result;
      }
   }

   public static ArrayList<ItemStack> getBaublesBelts(Entity entity) {
      ArrayList<ItemStack> result = new ArrayList();
      if (Compat.baubles && entity instanceof EntityPlayer) {
         IBaublesItemHandler inventory = BaublesApi.getBaublesHandler((EntityPlayer)entity);

         for(int i : BaubleType.BELT.getValidSlots()) {
            result.add(inventory.getStackInSlot(i));
         }

         return result;
      } else {
         return result;
      }
   }

   public static ArrayList<ItemStack> getBaublesHeadItems(Entity entity) {
      ArrayList<ItemStack> result = new ArrayList();
      if (Compat.baubles && entity instanceof EntityPlayer) {
         IBaublesItemHandler inventory = BaublesApi.getBaublesHandler((EntityPlayer)entity);

         for(int i : BaubleType.HEAD.getValidSlots()) {
            result.add(inventory.getStackInSlot(i));
         }

         return result;
      } else {
         return result;
      }
   }

   public static ArrayList<ItemStack> getBaublesBodyItems(Entity entity) {
      ArrayList<ItemStack> result = new ArrayList();
      if (Compat.baubles && entity instanceof EntityPlayer) {
         IBaublesItemHandler inventory = BaublesApi.getBaublesHandler((EntityPlayer)entity);

         for(int i : BaubleType.BODY.getValidSlots()) {
            result.add(inventory.getStackInSlot(i));
         }

         return result;
      } else {
         return result;
      }
   }

   public static ArrayList<ItemStack> getBaublesCharms(Entity entity) {
      ArrayList<ItemStack> result = new ArrayList();
      if (Compat.baubles && entity instanceof EntityPlayer) {
         IBaublesItemHandler inventory = BaublesApi.getBaublesHandler((EntityPlayer)entity);

         for(int i : BaubleType.CHARM.getValidSlots()) {
            result.add(inventory.getStackInSlot(i));
         }

         return result;
      } else {
         return result;
      }
   }

   public static ArrayList<ItemStack> getBaublesTrinkets(Entity entity) {
      ArrayList<ItemStack> result = new ArrayList();
      if (Compat.baubles && entity instanceof EntityPlayer) {
         IBaublesItemHandler inventory = BaublesApi.getBaublesHandler((EntityPlayer)entity);

         for(int i : BaubleType.TRINKET.getValidSlots()) {
            result.add(inventory.getStackInSlot(i));
         }

         return result;
      } else {
         return result;
      }
   }

   public static ITiamatPlayerInventory getTiamatInventory(Entity entity) {
      return !(entity instanceof EntityPlayer) ? null : TiamatInventoryAPI.getTiamatPlayerInventory((EntityPlayer)entity);
   }

   public static ItemStack getTiamatSheathedMainhand1(Entity entity) {
      return getTiamatSheathedMainhand1(getTiamatInventory(entity));
   }

   public static ItemStack getTiamatSheathedMainhand1(ITiamatPlayerInventory inventory) {
      return inventory == null ? null : inventory.getSheathedMainhand1();
   }

   public static void setTiamatSheathedMainhand1(Entity entity, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setSheathedMainhand1(stack);
      }

   }

   public static ItemStack getTiamatSheathedOffhand1(Entity entity) {
      return getTiamatSheathedOffhand1(getTiamatInventory(entity));
   }

   public static ItemStack getTiamatSheathedOffhand1(ITiamatPlayerInventory inventory) {
      return inventory == null ? null : inventory.getSheathedOffhand1();
   }

   public static void setTiamatSheathedOffhand1(Entity entity, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setSheathedOffhand1(stack);
      }

   }

   public static ItemStack getTiamatSheathedMainhand2(Entity entity) {
      return getTiamatSheathedMainhand2(getTiamatInventory(entity));
   }

   public static ItemStack getTiamatSheathedMainhand2(ITiamatPlayerInventory inventory) {
      return inventory == null ? null : inventory.getSheathedMainhand2();
   }

   public static void setTiamatSheathedMainhand2(Entity entity, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setSheathedMainhand2(stack);
      }

   }

   public static ItemStack getTiamatSheathedOffhand2(Entity entity) {
      return getTiamatSheathedOffhand2(getTiamatInventory(entity));
   }

   public static ItemStack getTiamatSheathedOffhand2(ITiamatPlayerInventory inventory) {
      return inventory == null ? null : inventory.getSheathedOffhand2();
   }

   public static void setTiamatSheathedOffhand2(Entity entity, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setSheathedOffhand2(stack);
      }

   }

   public static ArrayList<ItemStack> getTiamatArmor(Entity entity) {
      return getTiamatArmor(getTiamatInventory(entity));
   }

   public static ArrayList<ItemStack> getTiamatArmor(ITiamatPlayerInventory inventory) {
      return inventory == null ? new ArrayList() : new ArrayList(inventory.getTiamatArmor());
   }

   public static ItemStack getTiamatShoulderItem(Entity entity) {
      return getTiamatShoulderItem(getTiamatInventory(entity));
   }

   public static ItemStack getTiamatShoulderItem(ITiamatPlayerInventory inventory) {
      return inventory == null ? null : (ItemStack)inventory.getTiamatArmor().get(0);
   }

   public static void setTiamatShoulderItem(Entity entity, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setShoulders(stack);
      }

   }

   public static ItemStack getTiamatCapeItem(Entity entity) {
      return getTiamatCapeItem(getTiamatInventory(entity));
   }

   public static ItemStack getTiamatCapeItem(ITiamatPlayerInventory inventory) {
      return inventory == null ? null : (ItemStack)inventory.getTiamatArmor().get(1);
   }

   public static void setTiamatCapeItem(Entity entity, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setCape(stack);
      }

   }

   public static ArrayList<ItemStack> getTiamatQuickslots(Entity entity) {
      return getTiamatQuickslots(getTiamatInventory(entity));
   }

   public static ArrayList<ItemStack> getTiamatQuickslots(ITiamatPlayerInventory inventory) {
      return inventory == null ? new ArrayList() : new ArrayList(inventory.getQuickSlots());
   }

   public static void setTiamatQuickslot(Entity entity, int index, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setQuickSlot(index, stack);
      }

   }

   public static ItemStack getTiamatBackpack(Entity entity) {
      return getTiamatBackpack(getTiamatInventory(entity));
   }

   public static ItemStack getTiamatBackpack(ITiamatPlayerInventory inventory) {
      return inventory == null ? null : inventory.getBackpack();
   }

   public static void setTiamatBackpack(Entity entity, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setBackpack(stack);
      }

   }

   public static ItemStack getTiamatPet(Entity entity) {
      return getTiamatPet(getTiamatInventory(entity));
   }

   public static ItemStack getTiamatPet(ITiamatPlayerInventory inventory) {
      return inventory == null ? null : inventory.getPet();
   }

   public static void setTiamatPet(Entity entity, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setPet(stack);
      }

   }

   public static ItemStack getTiamatDeck(Entity entity) {
      return getTiamatDeck(getTiamatInventory(entity));
   }

   public static ItemStack getTiamatDeck(ITiamatPlayerInventory inventory) {
      return inventory == null ? null : inventory.getDeck();
   }

   public static void setTiamatDeck(Entity entity, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setDeck(stack);
      }

   }

   public static ArrayList<ItemStack> getTiamatClasses(Entity entity) {
      return getTiamatClasses(getTiamatInventory(entity));
   }

   public static ArrayList<ItemStack> getTiamatClasses(ITiamatPlayerInventory inventory) {
      return inventory == null ? new ArrayList() : new ArrayList(inventory.getPlayerClasses());
   }

   public static void setTiamatClass(Entity entity, int index, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setPlayerClass(index, stack);
      }

   }

   public static ArrayList<ItemStack> getTiamatOffensiveSkills(Entity entity) {
      return getTiamatOffensiveSkills(getTiamatInventory(entity));
   }

   public static ArrayList<ItemStack> getTiamatOffensiveSkills(ITiamatPlayerInventory inventory) {
      return inventory == null ? new ArrayList() : new ArrayList(inventory.getOffensiveSkills());
   }

   public static void setTiamatOffensiveSkill(Entity entity, int index, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setOffensiveSkill(index, stack);
      }

   }

   public static ArrayList<ItemStack> getTiamatUtilitySkills(Entity entity) {
      return getTiamatUtilitySkills(getTiamatInventory(entity));
   }

   public static ArrayList<ItemStack> getTiamatUtilitySkills(ITiamatPlayerInventory inventory) {
      return inventory == null ? new ArrayList() : new ArrayList(inventory.getUtilitySkills());
   }

   public static void setTiamatUtilitySkill(Entity entity, int index, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setUtilitySkill(index, stack);
      }

   }

   public static ItemStack getTiamatUltimateSkill(Entity entity) {
      return getTiamatUltimateSkill(getTiamatInventory(entity));
   }

   public static ItemStack getTiamatUltimateSkill(ITiamatPlayerInventory inventory) {
      return inventory == null ? null : inventory.getUltimateSkill();
   }

   public static void setTiamatUltimateSkill(Entity entity, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setUltimateSkill(stack);
      }

   }

   public static ArrayList<ItemStack> getTiamatPassiveSkills(Entity entity) {
      return getTiamatPassiveSkills(getTiamatInventory(entity));
   }

   public static ArrayList<ItemStack> getTiamatPassiveSkills(ITiamatPlayerInventory inventory) {
      return inventory == null ? new ArrayList() : new ArrayList(inventory.getPassiveSkills());
   }

   public static void setTiamatPassiveSkill(Entity entity, int index, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setPassiveSkill(index, stack);
      }

   }

   public static ArrayList<ItemStack> getTiamatGatheringProfessions(Entity entity) {
      return getTiamatGatheringProfessions(getTiamatInventory(entity));
   }

   public static ArrayList<ItemStack> getTiamatGatheringProfessions(ITiamatPlayerInventory inventory) {
      return inventory == null ? new ArrayList() : new ArrayList(inventory.getGatheringProfessions());
   }

   public static void setTiamatGatheringProfession(Entity entity, int index, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setGatheringProfession(index, stack);
      }

   }

   public static ArrayList<ItemStack> getTiamatCraftingProfessions(Entity entity) {
      return getTiamatCraftingProfessions(getTiamatInventory(entity));
   }

   public static ArrayList<ItemStack> getTiamatCraftingProfessions(ITiamatPlayerInventory inventory) {
      return inventory == null ? new ArrayList() : new ArrayList(inventory.getCraftingProfessions());
   }

   public static void setTiamatCraftingProfession(Entity entity, int index, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setCraftingProfession(index, stack);
      }

   }

   public static ArrayList<ItemStack> getTiamatRecipes(Entity entity) {
      return getTiamatRecipes(getTiamatInventory(entity));
   }

   public static ArrayList<ItemStack> getTiamatRecipes(ITiamatPlayerInventory inventory) {
      return inventory == null ? new ArrayList() : new ArrayList(inventory.getCraftingRecipes());
   }

   public static void setTiamatRecipe(Entity entity, int index, ItemStack stack) {
      ITiamatPlayerInventory inv = getTiamatInventory(entity);
      if (inv != null) {
         inv.setCraftingRecipe(index, stack);
      }

   }

   public static ArrayList<ItemStack> getAllTiamatItems(Entity entity) {
      return getAllTiamatItems(getTiamatInventory(entity));
   }

   public static ArrayList<ItemStack> getAllTiamatItems(ITiamatPlayerInventory inventory) {
      return inventory == null ? new ArrayList() : inventory.getAllItems();
   }

   public static ISkinType getSkinType(String skinTypeName) {
      return skinTypeRegistry == null ? null : skinTypeRegistry.getSkinTypeFromRegistryName(skinTypeName);
   }

   public static int getAWSkinSlotCount(Entity entity, String skinTypeName) {
      return getAWSkinSlotCount(entity, getSkinType(skinTypeName));
   }

   public static int getAWSkinSlotCount(Entity entity, ISkinType skinType) {
      IEntitySkinCapability wardrobeSkinHandler = ArmourersWorkshopApi.getEntitySkinCapability(entity);
      return wardrobeSkinHandler == null ? 0 : wardrobeSkinHandler.getSlotCountForSkinType(skinType);
   }

   public static ItemStack getAWSkin(Entity entity, String skinTypeName, int index) {
      return getAWSkin(entity, getSkinType(skinTypeName), index);
   }

   public static ItemStack getAWSkin(Entity entity, ISkinType skinType, int index) {
      IEntitySkinCapability wardrobeSkinHandler = ArmourersWorkshopApi.getEntitySkinCapability(entity);
      return wardrobeSkinHandler == null ? null : wardrobeSkinHandler.getSkinStack(skinType, index);
   }

   public static ArrayList<ItemStack> getAWSkinsOfType(Entity entity, String skinTypeName) {
      return getAWSkinsOfType(entity, getSkinType(skinTypeName));
   }

   public static ArrayList<ItemStack> getAWSkinsOfType(Entity entity, ISkinType skinType) {
      ArrayList<ItemStack> result = new ArrayList();
      int size = getAWSkinSlotCount(entity, skinType);

      for(int i = 0; i < size; ++i) {
         result.add(getAWSkin(entity, skinType, i));
      }

      return result;
   }

   public static ArrayList<ItemStack> getAWSkins(Entity entity) {
      ArrayList<ItemStack> result = new ArrayList();
      IEntitySkinCapability wardrobeSkinHandler = ArmourersWorkshopApi.getEntitySkinCapability(entity);
      if (wardrobeSkinHandler == null) {
         return result;
      } else {
         for(ISkinType skinTypeObject : wardrobeSkinHandler.getValidSkinTypes()) {
            result.addAll(getAWSkinsOfType(entity, skinTypeObject));
         }

         return result;
      }
   }

   public static ItemStack setAWSkin(Entity entity, String skinTypeName, int index, ItemStack newSkin) {
      return setAWSkin(entity, getSkinType(skinTypeName), index, newSkin);
   }

   public static ItemStack setAWSkin(Entity entity, ISkinType skinType, int index, ItemStack newSkin) {
      IEntitySkinCapability wardrobeSkinHandler = ArmourersWorkshopApi.getEntitySkinCapability(entity);
      if (wardrobeSkinHandler == null) {
         return null;
      } else {
         ItemStack result = wardrobeSkinHandler.setSkinStack(skinType, index, newSkin);
         FantasticAWAPI.refreshRenderModes(entity);
         return result;
      }
   }

   public static ISkinType[] getValidSkinTypes(Entity entity) {
      IEntitySkinCapability wardrobeSkinHandler = ArmourersWorkshopApi.getEntitySkinCapability(entity);
      return wardrobeSkinHandler == null ? null : wardrobeSkinHandler.getValidSkinTypes();
   }

   public static ArrayList<String> getValidSkinTypeNames(Entity entity) {
      ArrayList<String> result = new ArrayList();

      for(ISkinType skinTypeObject : getValidSkinTypes(entity)) {
         result.add(skinTypeObject.getName());
      }

      return result;
   }

   public static void syncAWWardrobeSkins(Entity entity, boolean syncToSelf, boolean syncToOthers) {
      if (!entity.world.isRemote) {
         IEntitySkinCapability wardrobeSkinHandler = ArmourersWorkshopApi.getEntitySkinCapability(entity);
         if (wardrobeSkinHandler != null) {
            if (syncToSelf && entity instanceof EntityPlayerMP) {
               wardrobeSkinHandler.syncToPlayer((EntityPlayerMP)entity);
            }

            if (syncToOthers) {
               wardrobeSkinHandler.syncToAllTracking();
            }

         }
      }
   }

   public static ItemStack getItem(Entity entity, String slot) {
      slot = slot.toLowerCase();
      if (slot.contains("bauble")) {
         ArrayList<ItemStack> list = getBaubles(entity);
         if (list.size() == 0) {
            return null;
         } else {
            try {
               int index = Integer.parseInt(slot.replace("bauble", "")) - 1;
               return index >= 0 && index < list.size() ? (ItemStack)list.get(index) : null;
            } catch (NumberFormatException var5) {
               return null;
            }
         }
      } else {
         byte var4 = -1;
         switch(slot.hashCode()) {
         case -1762175919:
            if (slot.equals("ultimate")) {
               var4 = 33;
            }
            break;
         case -1548738978:
            if (slot.equals("offhand")) {
               var4 = 1;
            }
            break;
         case -1357937223:
            if (slot.equals("class1")) {
               var4 = 27;
            }
            break;
         case -1357937222:
            if (slot.equals("class2")) {
               var4 = 28;
            }
            break;
         case -1176744694:
            if (slot.equals("gathering1")) {
               var4 = 36;
            }
            break;
         case -1176744693:
            if (slot.equals("gathering2")) {
               var4 = 37;
            }
            break;
         case -804835283:
            if (slot.equals("recipe10")) {
               var4 = 49;
            }
            break;
         case -804835282:
            if (slot.equals("recipe11")) {
               var4 = 50;
            }
            break;
         case -804835281:
            if (slot.equals("recipe12")) {
               var4 = 51;
            }
            break;
         case -804835280:
            if (slot.equals("recipe13")) {
               var4 = 52;
            }
            break;
         case -804835279:
            if (slot.equals("recipe14")) {
               var4 = 53;
            }
            break;
         case -804835278:
            if (slot.equals("recipe15")) {
               var4 = 54;
            }
            break;
         case -766268013:
            if (slot.equals("offhand1")) {
               var4 = 16;
            }
            break;
         case -766268012:
            if (slot.equals("offhand2")) {
               var4 = 18;
            }
            break;
         case -339967584:
            if (slot.equals("shoulder")) {
               var4 = 19;
            }
            break;
         case -243272823:
            if (slot.equals("mainhand1")) {
               var4 = 15;
            }
            break;
         case -243272822:
            if (slot.equals("mainhand2")) {
               var4 = 17;
            }
            break;
         case -7847512:
            if (slot.equals("mainhand")) {
               var4 = 0;
            }
            break;
         case 107022:
            if (slot.equals("leg")) {
               var4 = 4;
            }
            break;
         case 110879:
            if (slot.equals("pet")) {
               var4 = 25;
            }
            break;
         case 3046099:
            if (slot.equals("cape")) {
               var4 = 20;
            }
            break;
         case 3079337:
            if (slot.equals("deck")) {
               var4 = 26;
            }
            break;
         case 3138990:
            if (slot.equals("feet")) {
               var4 = 5;
            }
            break;
         case 3198432:
            if (slot.equals("head")) {
               var4 = 2;
            }
            break;
         case 65318388:
            if (slot.equals("offensive1")) {
               var4 = 29;
            }
            break;
         case 65318389:
            if (slot.equals("offensive2")) {
               var4 = 30;
            }
            break;
         case 94627585:
            if (slot.equals("chest")) {
               var4 = 3;
            }
            break;
         case 730635333:
            if (slot.equals("utility1")) {
               var4 = 31;
            }
            break;
         case 730635334:
            if (slot.equals("utility2")) {
               var4 = 32;
            }
            break;
         case 1082416227:
            if (slot.equals("recipe1")) {
               var4 = 40;
            }
            break;
         case 1082416228:
            if (slot.equals("recipe2")) {
               var4 = 41;
            }
            break;
         case 1082416229:
            if (slot.equals("recipe3")) {
               var4 = 42;
            }
            break;
         case 1082416230:
            if (slot.equals("recipe4")) {
               var4 = 43;
            }
            break;
         case 1082416231:
            if (slot.equals("recipe5")) {
               var4 = 44;
            }
            break;
         case 1082416232:
            if (slot.equals("recipe6")) {
               var4 = 45;
            }
            break;
         case 1082416233:
            if (slot.equals("recipe7")) {
               var4 = 46;
            }
            break;
         case 1082416234:
            if (slot.equals("recipe8")) {
               var4 = 47;
            }
            break;
         case 1082416235:
            if (slot.equals("recipe9")) {
               var4 = 48;
            }
            break;
         case 1099082827:
            if (slot.equals("hotbar1")) {
               var4 = 6;
            }
            break;
         case 1099082828:
            if (slot.equals("hotbar2")) {
               var4 = 7;
            }
            break;
         case 1099082829:
            if (slot.equals("hotbar3")) {
               var4 = 8;
            }
            break;
         case 1099082830:
            if (slot.equals("hotbar4")) {
               var4 = 9;
            }
            break;
         case 1099082831:
            if (slot.equals("hotbar5")) {
               var4 = 10;
            }
            break;
         case 1099082832:
            if (slot.equals("hotbar6")) {
               var4 = 11;
            }
            break;
         case 1099082833:
            if (slot.equals("hotbar7")) {
               var4 = 12;
            }
            break;
         case 1099082834:
            if (slot.equals("hotbar8")) {
               var4 = 13;
            }
            break;
         case 1099082835:
            if (slot.equals("hotbar9")) {
               var4 = 14;
            }
            break;
         case 1216574954:
            if (slot.equals("passive1")) {
               var4 = 34;
            }
            break;
         case 1216574955:
            if (slot.equals("passive2")) {
               var4 = 35;
            }
            break;
         case 1486599855:
            if (slot.equals("crafting1")) {
               var4 = 38;
            }
            break;
         case 1486599856:
            if (slot.equals("crafting2")) {
               var4 = 39;
            }
            break;
         case 1697897510:
            if (slot.equals("quickslot1")) {
               var4 = 21;
            }
            break;
         case 1697897511:
            if (slot.equals("quickslot2")) {
               var4 = 22;
            }
            break;
         case 1697897512:
            if (slot.equals("quickslot3")) {
               var4 = 23;
            }
            break;
         case 2121767808:
            if (slot.equals("backpack")) {
               var4 = 24;
            }
         }

         switch(var4) {
         case 0:
            return getVanillaMainhandItem(entity);
         case 1:
            ArrayList<ItemStack> list = getVanillaOffhandItems(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(0);
         case 2:
            return getVanillaHeadItem(entity);
         case 3:
            return getVanillaChestItem(entity);
         case 4:
            return getVanillaLegItem(entity);
         case 5:
            return getVanillaFootItem(entity);
         case 6:
            if (entity instanceof EntityPlayer) {
               return ((EntityPlayer)entity).inventory.getStackInSlot(0);
            }

            return null;
         case 7:
            if (entity instanceof EntityPlayer) {
               return ((EntityPlayer)entity).inventory.getStackInSlot(1);
            }

            return null;
         case 8:
            if (entity instanceof EntityPlayer) {
               return ((EntityPlayer)entity).inventory.getStackInSlot(2);
            }

            return null;
         case 9:
            if (entity instanceof EntityPlayer) {
               return ((EntityPlayer)entity).inventory.getStackInSlot(3);
            }

            return null;
         case 10:
            if (entity instanceof EntityPlayer) {
               return ((EntityPlayer)entity).inventory.getStackInSlot(4);
            }

            return null;
         case 11:
            if (entity instanceof EntityPlayer) {
               return ((EntityPlayer)entity).inventory.getStackInSlot(5);
            }

            return null;
         case 12:
            if (entity instanceof EntityPlayer) {
               return ((EntityPlayer)entity).inventory.getStackInSlot(6);
            }

            return null;
         case 13:
            if (entity instanceof EntityPlayer) {
               return ((EntityPlayer)entity).inventory.getStackInSlot(7);
            }

            return null;
         case 14:
            if (entity instanceof EntityPlayer) {
               return ((EntityPlayer)entity).inventory.getStackInSlot(8);
            }

            return null;
         case 15:
            return getTiamatSheathedMainhand1(entity);
         case 16:
            return getTiamatSheathedOffhand1(entity);
         case 17:
            return getTiamatSheathedMainhand2(entity);
         case 18:
            return getTiamatSheathedOffhand2(entity);
         case 19:
            return getTiamatShoulderItem(entity);
         case 20:
            return getTiamatCapeItem(entity);
         case 21:
            ArrayList<ItemStack> list = getTiamatQuickslots(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(0);
         case 22:
            ArrayList<ItemStack> list = getTiamatQuickslots(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(1);
         case 23:
            ArrayList<ItemStack> list = getTiamatQuickslots(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(2);
         case 24:
            return getTiamatBackpack(entity);
         case 25:
            return getTiamatPet(entity);
         case 26:
            return getTiamatDeck(entity);
         case 27:
            ArrayList<ItemStack> list = getTiamatClasses(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(0);
         case 28:
            ArrayList<ItemStack> list = getTiamatClasses(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(1);
         case 29:
            ArrayList<ItemStack> list = getTiamatOffensiveSkills(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(0);
         case 30:
            ArrayList<ItemStack> list = getTiamatOffensiveSkills(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(1);
         case 31:
            ArrayList<ItemStack> list = getTiamatUtilitySkills(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(0);
         case 32:
            ArrayList<ItemStack> list = getTiamatUtilitySkills(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(1);
         case 33:
            return getTiamatUltimateSkill(entity);
         case 34:
            ArrayList<ItemStack> list = getTiamatPassiveSkills(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(0);
         case 35:
            ArrayList<ItemStack> list = getTiamatPassiveSkills(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(1);
         case 36:
            ArrayList<ItemStack> list = getTiamatGatheringProfessions(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(0);
         case 37:
            ArrayList<ItemStack> list = getTiamatGatheringProfessions(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(1);
         case 38:
            ArrayList<ItemStack> list = getTiamatCraftingProfessions(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(0);
         case 39:
            ArrayList<ItemStack> list = getTiamatCraftingProfessions(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(1);
         case 40:
            ArrayList<ItemStack> list = getTiamatRecipes(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(0);
         case 41:
            ArrayList<ItemStack> list = getTiamatRecipes(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(1);
         case 42:
            ArrayList<ItemStack> list = getTiamatRecipes(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(2);
         case 43:
            ArrayList<ItemStack> list = getTiamatRecipes(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(3);
         case 44:
            ArrayList<ItemStack> list = getTiamatRecipes(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(4);
         case 45:
            ArrayList<ItemStack> list = getTiamatRecipes(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(5);
         case 46:
            ArrayList<ItemStack> list = getTiamatRecipes(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(6);
         case 47:
            ArrayList<ItemStack> list = getTiamatRecipes(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(7);
         case 48:
            ArrayList<ItemStack> list = getTiamatRecipes(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(8);
         case 49:
            ArrayList<ItemStack> list = getTiamatRecipes(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(9);
         case 50:
            ArrayList<ItemStack> list = getTiamatRecipes(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(10);
         case 51:
            ArrayList<ItemStack> list = getTiamatRecipes(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(11);
         case 52:
            ArrayList<ItemStack> list = getTiamatRecipes(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(12);
         case 53:
            ArrayList<ItemStack> list = getTiamatRecipes(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(13);
         case 54:
            ArrayList<ItemStack> list = getTiamatRecipes(entity);
            return list.size() == 0 ? null : (ItemStack)list.get(14);
         default:
            return null;
         }
      }
   }
}
