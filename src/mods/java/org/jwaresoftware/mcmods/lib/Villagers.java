package org.jwaresoftware.mcmods.lib;

import javax.annotation.Nullable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.Village;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public final class Villagers {
   public static final ResourceLocation FARMER_TYPE = new ResourceLocation("minecraft:farmer");
   public static final ResourceLocation NITWIT_TYPE = new ResourceLocation("minecraft:nitwit");

   public static final boolean isKnownVillagerProfession(int profession) {
      return profession >= 0 && profession < 6;
   }

   public static boolean isFarmer(@Nullable EntityVillager villager) {
      return villager != null && FARMER_TYPE.equals(villager.getProfessionForge().getRegistryName());
   }

   public static boolean isNitwit(@Nullable EntityVillager villager) {
      return villager != null && NITWIT_TYPE.equals(villager.getProfessionForge().getRegistryName());
   }

   @Nullable
   public static final Village getVillagerVillage(@Nullable EntityVillager villager) {
      if (villager != null) {
         try {
            return (Village)ReflectionHelper.getPrivateValue(EntityVillager.class, villager, new String[]{"villageObj", "village"});
         } catch (Throwable var2) {
            ;
         }
      }

      return null;
   }

   public static boolean isPlayerReputationTooLow(@Nullable EntityVillager villager, EntityPlayer player) {
      if (!SharedGlue.isRealPlayer(player)) {
         return true;
      } else {
         Village village = getVillagerVillage(villager);
         return village != null ? village.isPlayerReputationTooLow(player.getUniqueID()) : false;
      }
   }

   public static int getVillagerCareer(@Nullable EntityVillager villager) {
      if (villager != null) {
         try {
            return ((Integer)ReflectionHelper.getPrivateValue(EntityVillager.class, villager, new String[]{"careerId", "careerId"})).intValue();
         } catch (Throwable var2) {
            ;
         }
      }

      return -1;
   }

   public static int getVillagerCareerLevel(@Nullable EntityVillager villager) {
      if (villager != null) {
         try {
            return ((Integer)ReflectionHelper.getPrivateValue(EntityVillager.class, villager, new String[]{"careerLevel", "careerLevel"})).intValue();
         } catch (Throwable var2) {
            ;
         }
      }

      return -1;
   }

   private Villagers() {
   }

   public static final void init() {
   }
}
