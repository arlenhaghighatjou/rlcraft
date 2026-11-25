package de.impelon.disenchanter;

import de.impelon.disenchanter.block.TableVariant;
import de.impelon.disenchanter.inventory.IDisenchantmentItemHandler;
import de.impelon.disenchanter.item.ItemExperienceJar;
import de.impelon.disenchanter.proxy.CommonProxy;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class DisenchantingUtils {
   public DisenchantingUtils() {
   }

   public static ItemStack disenchantInInventory(IDisenchantmentItemHandler inventory, World world, BlockPos position, EaglercraftRandom random) {
      return disenchantInInventory(inventory, 0, world, position, random);
   }

   public static ItemStack disenchantInInventory(IDisenchantmentItemHandler inventory, int startIndex, World world, BlockPos position, EaglercraftRandom random) {
      return disenchantInInventory(inventory, startIndex, DisenchantingProperties.getPropertiesFromStateAt(world, position), (float)getEnchantingPower(world, position), world, position, random);
   }

   public static ItemStack disenchantInInventory(IDisenchantmentItemHandler inventory, int startIndex, DisenchantingProperties properties, float power, World world, BlockPos position, EaglercraftRandom random) {
      ItemStack result = simulateDisenchantingInInventory(inventory, false, startIndex, properties, power, random);
      if (!result.isEmpty() && !world.isRemote) {
         world.playSound((EntityPlayer)null, position, CommonProxy.disenchantmentTableUse, SoundCategory.BLOCKS, properties.is(TableVariant.AUTOMATIC) ? 0.5F : 1.0F, random.nextFloat() * 0.1F + 0.9F);
      }

      return result;
   }

   public static ItemStack simulateDisenchantingInInventory(IDisenchantmentItemHandler inventory, boolean ignoreEnchantmentLoss, World world, BlockPos position, EaglercraftRandom random) {
      return simulateDisenchantingInInventory(inventory, ignoreEnchantmentLoss, 0, world, position, random);
   }

   public static ItemStack simulateDisenchantingInInventory(IDisenchantmentItemHandler inventory, boolean ignoreEnchantmentLoss, int startIndex, World world, BlockPos position, EaglercraftRandom random) {
      return simulateDisenchantingInInventory(inventory, ignoreEnchantmentLoss, startIndex, DisenchantingProperties.getPropertiesFromStateAt(world, position), (float)getEnchantingPower(world, position), random);
   }

   public static ItemStack simulateDisenchantingInInventory(IDisenchantmentItemHandler inventory, boolean ignoreEnchantmentLoss, int startIndex, DisenchantingProperties properties, float power, EaglercraftRandom random) {
      if (properties.hasPersistantInventory() && !inventory.getOutputStack().isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack source = inventory.getSourceStack();
         ItemStack receiver = inventory.getReceiverStack();
         ItemStack target = getAppropriateResultTarget(receiver);
         if (target.isEmpty()) {
            return ItemStack.EMPTY;
         } else if (disenchant(source, target, ignoreEnchantmentLoss, startIndex, properties, power, random)) {
            if (receiver.getCount() > 1) {
               receiver.setCount(receiver.getCount() - 1);
            } else {
               receiver = ItemStack.EMPTY;
            }

            inventory.setReceiverStack(receiver);
            if (shouldBreakSource(source)) {
               if (source.getItem().equals(Items.ENCHANTED_BOOK)) {
                  source = new ItemStack(Items.BOOK);
               } else {
                  source = ItemStack.EMPTY;
               }
            }

            if (!source.isEmpty() && properties.is(TableVariant.VOIDING) && getAvailableEnchantmentIndices(source).isEmpty()) {
               source = ItemStack.EMPTY;
            }

            inventory.setSourceStack(source);
            if (target.getItem().equals(Items.ENCHANTED_BOOK) && getEnchantmentList(target) == null) {
               target = new ItemStack(Items.BOOK);
            }

            if (properties.hasPersistantInventory()) {
               inventory.setOutputStack(target);
            }

            return target;
         } else {
            return ItemStack.EMPTY;
         }
      }
   }

   public static boolean disenchant(ItemStack source, ItemStack target, boolean ignoreEnchantmentLoss, int startIndex, World world, BlockPos position, EaglercraftRandom random) {
      return disenchant(source, target, ignoreEnchantmentLoss, startIndex, DisenchantingProperties.getPropertiesFromStateAt(world, position), (float)getEnchantingPower(world, position), random);
   }

   public static boolean disenchant(ItemStack source, ItemStack target, boolean ignoreEnchantmentLoss, int startIndex, DisenchantingProperties properties, float power, EaglercraftRandom random) {
      int flatDmg = DisenchanterConfig.disenchanting.flatDamage;
      double durabilityDmg = DisenchanterConfig.disenchanting.maxDurabilityDamage;
      double reducibleDmg = DisenchanterConfig.disenchanting.maxDurabilityDamageReducible;
      double dmgMultiplier = properties.is(TableVariant.AUTOMATIC) ? DisenchanterConfig.disenchanting.machineDamageMultiplier : 1.0D;
      boolean hasTransferredEnchantments = false;

      List<Integer> indices;
      while(!(indices = getAvailableEnchantmentIndices(source)).isEmpty()) {
         int index = Math.abs(((Integer)indices.get(startIndex % indices.size())).intValue());
         if (!transferEnchantment(source, target, index, ignoreEnchantmentLoss, power, random)) {
            break;
         }

         hasTransferredEnchantments = true;
         source.attemptDamageItem((int)(dmgMultiplier * ((double)flatDmg + (double)source.getMaxDamage() * durabilityDmg + (double)source.getMaxDamage() * (reducibleDmg / (double)power))), random, (EntityPlayerMP)null);
         if (shouldBreakSource(source) || !properties.is(TableVariant.BULKDISENCHANTING)) {
            break;
         }
      }

      return hasTransferredEnchantments;
   }

   public static boolean transferEnchantment(ItemStack source, ItemStack target, int index, boolean ignoreEnchantmentLoss, float power, EaglercraftRandom random) {
      if (!source.isEmpty() && !target.isEmpty() && source.getTagCompound() != null) {
         NBTTagList enchants = getEnchantmentList(source);
         if (enchants == null) {
            return false;
         } else {
            if (enchants.tagCount() > 0) {
               index = Math.min(Math.abs(index), enchants.tagCount() - 1);
               NBTTagCompound enchant = enchants.getCompoundTagAt(index);
               int id = enchant.getInteger("id");
               int lvl = enchant.getInteger("lvl");
               EnchantmentData enchantment = new EnchantmentData(Enchantment.getEnchantmentByID(id), lvl);
               double enchantmentLoss = DisenchanterConfig.disenchanting.enchantmentLossChance;
               if (ignoreEnchantmentLoss || (double)random.nextFloat() > enchantmentLoss) {
                  if (target.getItem().equals(Items.ENCHANTED_BOOK)) {
                     ItemEnchantedBook.addEnchantment(target, enchantment);
                  } else if (target.getItem().equals(CommonProxy.itemExperienceJar)) {
                     ItemExperienceJar.ensureValidTag(target);
                     int storedXP = ItemExperienceJar.getStoredExperience(target);
                     int gainedXP = getExperienceForEnchantment(enchantment.enchantment, enchantment.enchantmentLevel, power);
                     if (storedXP + gainedXP < 0) {
                        return false;
                     }

                     ItemExperienceJar.setStoredExperienceClamped(target, storedXP + gainedXP);
                  }
               }

               enchants.removeTag(index);
               double repairCostMultiplier = DisenchanterConfig.disenchanting.repairCostMultiplier;
               source.setRepairCost((int)((double)source.getRepairCost() * repairCostMultiplier));
            }

            if (enchants.tagCount() <= 0) {
               if (isEnchantmentStorage(source)) {
                  source.getTagCompound().removeTag("StoredEnchantments");
               } else {
                  source.getTagCompound().removeTag("ench");
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static List<Integer> getAvailableEnchantmentIndices(ItemStack itemstack) {
      List<Integer> available = new ArrayList();
      NBTTagList enchants = getEnchantmentList(itemstack);
      if (enchants == null) {
         return available;
      } else {
         String[] disabledItems = DisenchanterConfig.disenchanting.disabledItems;

         for(String disabeledName : disabledItems) {
            String[] splitted = disabeledName.split("\\[r\\]", 2);
            if (splitted.length > 1) {
               if (itemstack.getItem().getRegistryName().toString().matches(splitted[1])) {
                  return available;
               }
            } else if (itemstack.getItem().getRegistryName().toString().equalsIgnoreCase(splitted[0])) {
               return available;
            }
         }

         String[] disabledEnchantments = DisenchanterConfig.disenchanting.disabledEnchantments;

         for(int index = 0; index < enchants.tagCount(); ++index) {
            NBTTagCompound enchant = enchants.getCompoundTagAt(index);
            Enchantment enchantment = Enchantment.getEnchantmentByID(enchant.getInteger("id"));
            boolean valid = DisenchanterConfig.disenchanting.disableCurses ? enchantment.isCurse() : true;

            for(String disabeledName : disabledEnchantments) {
               if (!valid) {
                  break;
               }

               String[] splitted = disabeledName.split("\\[r\\]", 2);
               if (splitted.length > 1) {
                  if (enchantment.getRegistryName().toString().matches(splitted[1])) {
                     valid = false;
                  }
               } else if (enchantment.getRegistryName().toString().equalsIgnoreCase(splitted[0])) {
                  valid = false;
               }
            }

            if (valid) {
               available.add(Integer.valueOf(index));
            }
         }

         return available;
      }
   }

   public static NBTTagList getEnchantmentList(ItemStack itemstack) {
      if (!itemstack.isEmpty() && itemstack.getTagCompound() != null) {
         if (itemstack.getTagCompound().getTag("InfiTool") != null && DisenchanterConfig.disenchanting.enableTCBehaviour) {
            return null;
         } else if (itemstack.getTagCompound().getTag("TinkerData") != null && DisenchanterConfig.disenchanting.enableTCBehaviour) {
            return null;
         } else if (itemstack.getTagCompound().getTag("ench") != null) {
            return (NBTTagList)itemstack.getTagCompound().getTag("ench");
         } else {
            return itemstack.getTagCompound().getTag("StoredEnchantments") != null ? (NBTTagList)itemstack.getTagCompound().getTag("StoredEnchantments") : null;
         }
      } else {
         return null;
      }
   }

   public static int getExperienceForEnchantment(Enchantment enchantment, int level, float power) {
      int min = enchantment.getMinEnchantability(level);
      int max = enchantment.getMaxEnchantability(level);
      int flatXP = DisenchanterConfig.disenchanting.flatExperience;
      double powerXP = DisenchanterConfig.disenchanting.powerExperience;
      double enchantabilityMultiplier = DisenchanterConfig.disenchanting.powerEnchantabilityExperienceMultiplier;
      double minEnchantabilityXP = DisenchanterConfig.disenchanting.minEnchantabilityExperience;
      double maxEnchantabilityXP = DisenchanterConfig.disenchanting.maxEnchantabilityExperience;
      double enchantabilityXP = (double)min * minEnchantabilityXP + (double)max * maxEnchantabilityXP;
      return flatXP + (int)(enchantabilityXP + enchantabilityXP * enchantabilityMultiplier * (double)power + powerXP * (double)power);
   }

   public static ItemStack getAppropriateResultTarget(ItemStack receiver) {
      if (!receiver.isEmpty()) {
         if (!DisenchanterConfig.experienceJar.jarDisenchantingOnly && receiver.getItem().equals(Items.BOOK)) {
            return new ItemStack(Items.ENCHANTED_BOOK);
         }

         if (receiver.getItem().equals(CommonProxy.itemExperienceJar) && (ItemExperienceJar.hasAvailableExperienceCapacity(receiver) || ItemExperienceJar.isOverloadActive(receiver))) {
            return receiver.copy();
         }
      }

      return ItemStack.EMPTY;
   }

   public static boolean isEnchantmentStorage(ItemStack itemstack) {
      return itemstack.getTagCompound().getTag("StoredEnchantments") != null;
   }

   public static boolean isItemStackBroken(ItemStack itemstack) {
      return itemstack.isItemStackDamageable() && itemstack.getItemDamage() > itemstack.getMaxDamage();
   }

   public static boolean shouldBreakSource(ItemStack source) {
      if (source.getItem().equals(Items.ENCHANTED_BOOK) && getEnchantmentList(source) == null) {
         return true;
      } else if (isItemStackBroken(source)) {
         return true;
      } else {
         return DisenchanterConfig.disenchanting.destroyNonDamageableItems && !source.isItemStackDamageable();
      }
   }

   public static int getEnchantingPower(World w, BlockPos pos) {
      float power = 1.0F;

      for(int blockZ = -1; blockZ <= 1; ++blockZ) {
         for(int blockX = -1; blockX <= 1 && power < 15.0F; ++blockX) {
            if ((blockZ != 0 || blockX != 0) && w.isAirBlock(pos.add(blockX, 0, blockZ)) && w.isAirBlock(pos.add(blockX, 1, blockZ))) {
               power = power + ForgeHooks.getEnchantPower(w, pos.add(blockX * 2, 0, blockZ * 2));
               power = power + ForgeHooks.getEnchantPower(w, pos.add(blockX * 2, 1, blockZ * 2));
               if (blockX != 0 && blockZ != 0) {
                  power = power + ForgeHooks.getEnchantPower(w, pos.add(blockX * 2, 0, blockZ));
                  power = power + ForgeHooks.getEnchantPower(w, pos.add(blockX * 2, 1, blockZ));
                  power = power + ForgeHooks.getEnchantPower(w, pos.add(blockX, 0, blockZ * 2));
                  power = power + ForgeHooks.getEnchantPower(w, pos.add(blockX, 1, blockZ * 2));
               }
            }
         }

         if (power >= 15.0F) {
            power = 15.0F;
            break;
         }
      }

      return (int)power;
   }
}
