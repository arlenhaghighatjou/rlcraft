package c4.corpsecomplex.common.modules.inventory.helpers;

import c4.corpsecomplex.CorpseComplex;
import c4.corpsecomplex.common.modules.compatibility.enderio.EnderIOIntegration;
import c4.corpsecomplex.common.modules.inventory.InventoryModule;
import c4.corpsecomplex.common.modules.inventory.enchantment.EnchantmentModule;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public final class DeathStackHelper {
   private static EaglercraftRandom generator = new EaglercraftRandom();

   private DeathStackHelper() {
   }

   public static ItemStack stackToStore(EntityPlayer player, ItemStack stack, boolean cfgStore) {
      return stackToStore(player, stack, cfgStore, false);
   }

   public static ItemStack stackToStore(EntityPlayer player, ItemStack stack, boolean cfgStore, boolean isMainInventory) {
      boolean essential = isEssential(stack);
      boolean cursed = !essential && isCursed(stack);
      boolean store = cfgStore && !cursed || essential;
      if (EnchantmentModule.registerEnchant) {
         int level = EnchantmentHelper.getEnchantmentLevel(EnchantmentModule.soulbound, stack);
         if (level != 0) {
            essential = handleSoulbound(stack, level);
            cursed = !essential && cursed;
            if (!store && essential) {
               store = true;
            }
         }
      }

      if (cursed && InventoryModule.destroyCursed) {
         destroyStack(stack);
         return stack;
      } else {
         if (InventoryModule.dropLoss > 0.0D || InventoryModule.keptLoss > 0.0D) {
            loseDurability(player, stack, store);
         }

         if (InventoryModule.dropDrain > 0.0D || InventoryModule.keptDrain > 0.0D) {
            loseEnergy(stack, store);
         }

         if (stack.isEmpty()) {
            return ItemStack.EMPTY;
         } else if (!store) {
            return ItemStack.EMPTY;
         } else if (essential || InventoryModule.randomDrop <= 0.0D || !isMainInventory && InventoryModule.randomDropOnlyMain) {
            ItemStack stack1 = stack.copy();
            stack.setCount(0);
            return stack1;
         } else {
            int keepAmount = stack.getCount();
            keepAmount = keepAmount - randomlyDrop(stack);
            return stack.splitStack(keepAmount);
         }
      }
   }

   public static void destroyStack(ItemStack stack) {
      stack.setCount(0);
   }

   public static int randomlyDrop(ItemStack stack) {
      int dropAmount = 0;

      for(int i = 0; i < stack.getCount(); ++i) {
         if (generator.nextDouble() < InventoryModule.randomDrop) {
            ++dropAmount;
         }
      }

      return dropAmount;
   }

   public static void randomlyDestroy(ItemStack stack) {
      int destroyAmount = 0;

      for(int i = 0; i < stack.getCount(); ++i) {
         if (generator.nextDouble() < InventoryModule.randomDestroy) {
            ++destroyAmount;
         }
      }

      stack.shrink(destroyAmount);
   }

   public static void loseDurability(EntityPlayer player, ItemStack stack, boolean store) {
      if (stack.isItemStackDamageable()) {
         int limit = InventoryModule.durabilityLossLimiter ? stack.getMaxDamage() - stack.getItemDamage() - 1 : stack.getMaxDamage();
         double actualLoss = 0.0D;
         if (store) {
            if (InventoryModule.difficultyKeptLoss) {
               if (player.world.getDifficulty() == EnumDifficulty.EASY) {
                  actualLoss = InventoryModule.keptLossEasy;
               } else if (player.world.getDifficulty() == EnumDifficulty.NORMAL) {
                  actualLoss = InventoryModule.keptLossNormal;
               } else if (player.world.getDifficulty() == EnumDifficulty.HARD) {
                  actualLoss = InventoryModule.keptLossHard;
               }
            } else {
               actualLoss = InventoryModule.keptLoss;
            }
         } else if (InventoryModule.difficultyDropLoss) {
            if (player.world.getDifficulty() == EnumDifficulty.EASY) {
               actualLoss = InventoryModule.dropLossEasy;
            } else if (player.world.getDifficulty() == EnumDifficulty.NORMAL) {
               actualLoss = InventoryModule.dropLossNormal;
            } else if (player.world.getDifficulty() == EnumDifficulty.HARD) {
               actualLoss = InventoryModule.dropLossHard;
            }
         } else {
            actualLoss = InventoryModule.dropLoss;
         }

         int loss = (int)Math.round((double)stack.getMaxDamage() * actualLoss);
         stack.damageItem(Math.min(loss, limit), player);
      }
   }

   public static void loseEnergy(ItemStack stack, boolean store) {
      IEnergyStorage energy = (IEnergyStorage)stack.getCapability(CapabilityEnergy.ENERGY, (EnumFacing)null);
      if (energy != null) {
         int energyToLose;
         if (store) {
            energyToLose = (int)Math.round((double)energy.getMaxEnergyStored() * InventoryModule.keptDrain);
         } else {
            energyToLose = (int)Math.round((double)energy.getMaxEnergyStored() * InventoryModule.dropDrain);
         }

         int energyExtracted;
         for(boolean hasUpgrade = CorpseComplex.isEnderIOLoaded && EnderIOIntegration.hasPowerUpgrade(stack); energyToLose > 0 && energy.getEnergyStored() > 0; energyToLose -= energyExtracted) {
            energyExtracted = hasUpgrade ? EnderIOIntegration.extractEnergy(stack, energyToLose, false) : energy.extractEnergy(energyToLose, false);
            if (energyExtracted == 0) {
               break;
            }
         }

      }
   }

   public static boolean isEssential(ItemStack stack) {
      for(String s : InventoryModule.essentialItems) {
         ResourceLocation name = stack.getItem().getRegistryName();
         if (name != null && s.equals(name.toString())) {
            return true;
         }
      }

      return false;
   }

   public static boolean isCursed(ItemStack stack) {
      if (EnchantmentHelper.hasVanishingCurse(stack)) {
         return true;
      } else {
         for(String s : InventoryModule.cursedItems) {
            ResourceLocation name = stack.getItem().getRegistryName();
            if (name != null && s.equals(name.toString())) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean handleSoulbound(ItemStack stack, int level) {
      double savePercent = EnchantmentModule.baseSave + EnchantmentModule.extraPerLevel * (double)(level - 1);
      boolean activated = false;
      if (generator.nextDouble() < savePercent) {
         activated = true;
      }

      if (EnchantmentModule.levelDrop != 0.0D && activated) {
         if (generator.nextDouble() < EnchantmentModule.levelDrop) {
            level = Math.max(0, level - 1);
         }

         Map<Enchantment, Integer> enchMap = EnchantmentHelper.getEnchantments(stack);
         enchMap.remove(EnchantmentModule.soulbound);
         if (level > 0) {
            enchMap.put(EnchantmentModule.soulbound, Integer.valueOf(level));
         }

         EnchantmentHelper.setEnchantments(enchMap, stack);
      }

      return activated;
   }
}
