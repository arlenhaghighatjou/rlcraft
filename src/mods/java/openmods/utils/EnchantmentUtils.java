package openmods.utils;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class EnchantmentUtils {
   public EnchantmentUtils() {
   }

   public static int getPlayerXP(EntityPlayer player) {
      return (int)((float)getExperienceForLevel(player.experienceLevel) + player.experience * (float)player.xpBarCap());
   }

   public static void addPlayerXP(EntityPlayer player, int amount) {
      int experience = getPlayerXP(player) + amount;
      player.experienceTotal = experience;
      player.experienceLevel = getLevelForExperience(experience);
      int expForLevel = getExperienceForLevel(player.experienceLevel);
      player.experience = (float)(experience - expForLevel) / (float)player.xpBarCap();
   }

   public static int xpBarCap(int level) {
      if (level >= 30) {
         return 112 + (level - 30) * 9;
      } else {
         return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
      }
   }

   private static int sum(int n, int a0, int d) {
      return n * (2 * a0 + (n - 1) * d) / 2;
   }

   public static int getExperienceForLevel(int level) {
      if (level == 0) {
         return 0;
      } else if (level <= 15) {
         return sum(level, 7, 2);
      } else {
         return level <= 30 ? 315 + sum(level - 15, 37, 5) : 1395 + sum(level - 30, 112, 9);
      }
   }

   public static int getXpToNextLevel(int level) {
      int levelXP = getLevelForExperience(level);
      int nextXP = getExperienceForLevel(level + 1);
      return nextXP - levelXP;
   }

   public static int getLevelForExperience(int targetXp) {
      int level = 0;

      while(true) {
         int xpToNextLevel = xpBarCap(level);
         if (targetXp < xpToNextLevel) {
            return level;
         }

         ++level;
         targetXp -= xpToNextLevel;
      }
   }

   public static float getPower(World world, BlockPos position) {
      float power = 0.0F;

      for(int deltaZ = -1; deltaZ <= 1; ++deltaZ) {
         for(int deltaX = -1; deltaX <= 1; ++deltaX) {
            if ((deltaZ != 0 || deltaX != 0) && world.isAirBlock(position.add(deltaX, 0, deltaZ)) && world.isAirBlock(position.add(deltaX, 1, deltaZ))) {
               power = power + ForgeHooks.getEnchantPower(world, position.add(deltaX * 2, 0, deltaZ * 2));
               power = power + ForgeHooks.getEnchantPower(world, position.add(deltaX * 2, 1, deltaZ * 2));
               if (deltaX != 0 && deltaZ != 0) {
                  power = power + ForgeHooks.getEnchantPower(world, position.add(deltaX * 2, 0, deltaZ));
                  power = power + ForgeHooks.getEnchantPower(world, position.add(deltaX * 2, 1, deltaZ));
                  power = power + ForgeHooks.getEnchantPower(world, position.add(deltaX, 0, deltaZ * 2));
                  power = power + ForgeHooks.getEnchantPower(world, position.add(deltaX, 1, deltaZ * 2));
               }
            }
         }
      }

      return power;
   }

   public static void addAllBooks(Enchantment enchantment, List<ItemStack> items) {
      for(int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); ++i) {
         items.add(ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(enchantment, i)));
      }

   }
}
