package org.jwaresoftware.mcmods.lib;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public final class XpCalculations {
   public static final int LEVEL_ONE_XP = 17;
   public static final int LEVEL_ONE_XP_MENDABILITY = 34;
   public static final float XP_TO_MILLIBUCKET_MULTIPLIER = 31.25F;
   public static final float MIN_XP_FOR_MILLIBUCKET = 0.032F;

   public static final int getExperienceForLevel(int level) {
      if (level <= 0) {
         return 0;
      } else if (level > 0 && level < 16) {
         return level * 17;
      } else {
         return level > 15 && level < 31 ? (int)(1.5D * Math.pow((double)level, 2.0D) - 29.5D * (double)level + 360.0D) : (int)(3.5D * Math.pow((double)level, 2.0D) - 151.5D * (double)level + 2220.0D);
      }
   }

   public static final int getLevelForExperience(int experience) {
      int i;
      for(i = 0; getExperienceForLevel(i) <= experience; ++i) {
         ;
      }

      return i - 1;
   }

   public static final int getXpToNextLevel(int level) {
      return getExperienceForLevel(level + 1) - getLevelForExperience(level);
   }

   public static final int getPlayerXP(@Nullable EntityPlayer player) {
      return player == null ? 0 : (int)((float)getExperienceForLevel(player.experienceLevel) + player.experience * (float)player.xpBarCap());
   }

   public static final int getXpToNextLevel(@Nonnull EntityPlayer player) {
      int playerXp = getPlayerXP(player);
      int curLevel = getLevelForExperience(playerXp);
      return 1 + (getExperienceForLevel(curLevel + 1) - playerXp);
   }

   public static final boolean addXpToPlayer(EntityPlayer player, int amount) {
      int current_experience = getPlayerXP(player);
      if (current_experience <= 0 && amount <= 0) {
         return false;
      } else {
         int experience = current_experience + amount;
         if (experience < 0) {
            experience = 0;
         }

         player.experienceTotal = experience;
         player.experienceLevel = getLevelForExperience(experience);
         int expForLevel = getExperienceForLevel(player.experienceLevel);
         player.experience = (float)(experience - expForLevel) / (float)player.xpBarCap();
         return true;
      }
   }

   public static final void spawnXP(@Nonnull World world, double x, double y, double z, int amount, @Nullable EaglercraftRandom rand) {
      float dx = rand != null ? rand.nextFloat() * 0.8F + 0.1F : 0.0F;
      float dy = rand != null ? rand.nextFloat() * 0.8F + 0.1F : 0.0F;
      float dz = rand != null ? rand.nextFloat() * 0.8F + 0.1F : 0.0F;
      EntityXPOrb orb = new EntityXPOrb(world, x + (double)dx, y + (double)dy, z + (double)dz, amount);
      world.spawnEntity(orb);
   }

   public static final void spawnXPBurst(@Nonnull World world, double x, double y, double z, int amount, @Nullable EaglercraftRandom rand) {
      while(amount > 0) {
         int subamount = EntityXPOrb.getXPSplit(amount);
         amount -= subamount;
         spawnXP(world, x, y, z, subamount, rand);
      }

   }

   public static final int durabilityToXp(int durability) {
      return durability / 2;
   }

   public static final int xpToDurability(int xp) {
      return xp * 2;
   }

   public static final int xpToMillibuckets(float xp) {
      return (int)(Math.abs(xp) * 31.25F);
   }

   public static final int clampRepairCost(int cost, int max) {
      if (max <= 0) {
         max = SharedGlue.HIGH_ANVIL_REPAIR_XP_LEVEL_COST();
      }

      if (cost > max) {
         cost = max;
      }

      if (cost < 0) {
         cost = 0;
      }

      return cost;
   }

   public static final int clampRepairCost(int cost) {
      return clampRepairCost(cost, -1);
   }

   private XpCalculations() {
   }
}
