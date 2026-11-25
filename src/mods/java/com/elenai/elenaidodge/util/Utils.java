package com.elenai.elenaidodge.util;

import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.skill.Skill;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import com.elenai.elenaidodge.ElenaiDodge;
import com.elenai.elenaidodge.ModConfig;
import com.elenai.elenaidodge.api.DodgeEvent;
import com.elenai.elenaidodge.api.LedgeGrabEvent;
import com.elenai.elenaidodge.api.WallJumpEvent;
import com.elenai.elenaidodge.capability.weight.IWeight;
import com.elenai.elenaidodge.capability.weight.WeightProvider;
import com.elenai.elenaidodge.effects.ServerDodgeEffects;
import com.elenai.elenaidodge.effects.ServerLedgeGrabEffects;
import com.elenai.elenaidodge.effects.ServerWallJumpEffects;
import com.elenai.elenaidodge.network.PacketHandler;
import com.elenai.elenaidodge.network.message.CDodgeEffectsMessage;
import com.elenai.elenaidodge.network.message.CInitPlayerMessage;
import com.elenai.elenaidodge.network.message.CUpdateConfigMessage;
import com.elenai.elenaidodge.network.message.CVelocityMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.Loader;

public class Utils {
   public Utils() {
   }

   public static void setPlayerVelocity(double x, double y, double z, EntityPlayer player, boolean useExistingVelocity) {
      PacketHandler.instance.sendTo(new CVelocityMessage(x, y, z, useExistingVelocity), (EntityPlayerMP)player);
   }

   public static void handleDodge(DodgeEvent.Direction direction, DodgeEvent.ServerDodgeEvent event, EntityPlayerMP player) {
      double f = event.getForce();
      double motionX;
      double motionZ;
      switch(direction) {
      case LEFT:
         motionX = (double)(MathHelper.cos(player.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(0.017453294F)) * f;
         motionZ = -((double)(-MathHelper.sin(player.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(0.017453294F)) * f);
         break;
      case RIGHT:
         motionX = -((double)(MathHelper.cos(player.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(0.017453294F)) * f);
         motionZ = (double)(-MathHelper.sin(player.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(0.017453294F)) * f;
         break;
      case FORWARD:
         motionX = (double)(-MathHelper.sin(player.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(0.017453294F)) * f;
         motionZ = (double)(MathHelper.cos(player.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(0.017453294F)) * f;
         break;
      case BACK:
         motionX = -((double)(-MathHelper.sin(player.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(0.017453294F)) * f);
         motionZ = -((double)(MathHelper.cos(player.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(0.017453294F)) * f);
         break;
      default:
         motionX = 0.0D;
         motionZ = 0.0D;
         ElenaiDodge.LOG.error("DodgeEvent Posted and Received but no direction given!");
      }

      setPlayerVelocity(motionX, ModConfig.common.balance.verticality, motionZ, player, ModConfig.common.balance.useExistingVelocity);
      ServerDodgeEffects.run(player);
      PacketHandler.instance.sendTo(new CDodgeEffectsMessage(), player);
   }

   public static double calculateForce(EntityPlayer player) {
      return ModConfig.common.balance.force;
   }

   public static void initPlayer(EntityPlayer player) {
      PacketHandler.instance.sendTo(new CInitPlayerMessage(), (EntityPlayerMP)player);
   }

   public static void updateClientConfig(EntityPlayerMP player) {
      PacketHandler.instance.sendTo(new CUpdateConfigMessage(ModConfig.common.weights.enable ? getWeightTier(player).getCooldown() : ModConfig.common.balance.regenSpeed, arrayToString(ModConfig.common.weights.weights), ModConfig.common.weights.enable), player);
   }

   public static void updateClientConfig() {
      PacketHandler.instance.sendToAll(new CUpdateConfigMessage(ModConfig.common.balance.regenSpeed, arrayToString(ModConfig.common.weights.weights), ModConfig.common.weights.enable));
   }

   public static int getTotalEnchantmentLevel(Enchantment enchantment, EntityLivingBase entity) {
      Iterable<ItemStack> iterable = enchantment.getEntityEquipment(entity);
      if (iterable == null) {
         return 0;
      } else {
         int i = 0;

         for(ItemStack itemstack : iterable) {
            int j = EnchantmentHelper.getEnchantmentLevel(enchantment, itemstack);
            i += j;
         }

         return i;
      }
   }

   public static String arrayToString(String[] string) {
      if (string.length <= 0) {
         return "";
      } else {
         StringBuilder stringBuilder = new StringBuilder();

         for(String n : string) {
            stringBuilder.append("").append(n.replace("'", "\\'")).append(",");
         }

         stringBuilder.deleteCharAt(stringBuilder.length() - 1);
         return stringBuilder.toString();
      }
   }

   public static boolean dodgeTraitUnlocked(EntityPlayer player) {
      return Loader.isModLoaded("reskillable") ? PlayerDataHandler.get(player).getSkillInfo((Skill)ReskillableRegistries.SKILLS.getValue(new ResourceLocation("reskillable", "agility"))).isUnlocked((Unlockable)ReskillableRegistries.UNLOCKABLES.getValue(new ResourceLocation("elenaidodge", "dodge"))) : true;
   }

   public static boolean wallJumpTraitUnlocked(EntityPlayer player) {
      return Loader.isModLoaded("reskillable") ? PlayerDataHandler.get(player).getSkillInfo((Skill)ReskillableRegistries.SKILLS.getValue(new ResourceLocation("reskillable", "agility"))).isUnlocked((Unlockable)ReskillableRegistries.UNLOCKABLES.getValue(new ResourceLocation("elenaidodge", "walljump"))) : true;
   }

   public static boolean ledgeGrabTraitUnlocked(EntityPlayer player) {
      return Loader.isModLoaded("reskillable") ? PlayerDataHandler.get(player).getSkillInfo((Skill)ReskillableRegistries.SKILLS.getValue(new ResourceLocation("reskillable", "agility"))).isUnlocked((Unlockable)ReskillableRegistries.UNLOCKABLES.getValue(new ResourceLocation("elenaidodge", "ledgegrab"))) : true;
   }

   public static WeightTier getWeightTier(EntityPlayer player) {
      List<String> tiers = new ArrayList();
      Collections.addAll(tiers, ModConfig.common.weights.tiers);
      Collections.reverse(tiers);

      for(String tier : tiers) {
         String[] tierValue = tier.split("[:\\|]");
         IWeight w = (IWeight)player.getCapability(WeightProvider.WEIGHT_CAP, (EnumFacing)null);
         if (w.getWeight() >= Double.valueOf(tierValue[0]).doubleValue()) {
            return new WeightTier(Double.valueOf(tierValue[0]).doubleValue(), Integer.valueOf(tierValue[1]).intValue(), Double.valueOf(tierValue[2]).doubleValue());
         }
      }

      return new WeightTier(0.0D, ModConfig.common.balance.regenSpeed, ModConfig.common.balance.force);
   }

   public static boolean hasGamestage(EntityPlayer player) {
      return !Loader.isModLoaded("gamestages") ? true : GameStageHelper.hasStage(player, ModConfig.common.gamestages.name);
   }

   public static boolean hasAirborneGamestage(EntityPlayer player) {
      return !Loader.isModLoaded("gamestages") ? false : GameStageHelper.hasStage(player, ModConfig.common.gamestages.nameAirborne);
   }

   public static void handleWallJump(WallJumpEvent.ServerWallJumpEvent event, EntityPlayerMP player) {
      double f = event.getForce();
      WallJumpEvent.Direction dir = event.getDirection();
      double motionX = (double)(-MathHelper.sin(player.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(0.017453294F)) * f;
      double motionZ = (double)(MathHelper.cos(player.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(0.017453294F)) * f;
      switch(dir) {
      case NORTH:
         motionZ -= ModConfig.common.wallJump.outwardsForce;
         break;
      case EAST:
         motionX += ModConfig.common.wallJump.outwardsForce;
         break;
      case SOUTH:
         motionZ += ModConfig.common.wallJump.outwardsForce;
         break;
      case WEST:
         motionX -= ModConfig.common.wallJump.outwardsForce;
      }

      setPlayerVelocity(motionX, ModConfig.common.wallJump.verticality, motionZ, player, ModConfig.common.wallJump.useExistingVelocity);
      ServerWallJumpEffects.run(player);
   }

   public static void handleLedgeGrab(LedgeGrabEvent.ServerLedgeGrabEvent event, EntityPlayerMP player) {
      double f = event.getForce();
      double motionX = (double)(-MathHelper.sin(player.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(0.017453294F)) * f;
      double motionZ = (double)(MathHelper.cos(player.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(0.017453294F)) * f;
      setPlayerVelocity(motionX, ModConfig.common.ledgeGrab.upwardsForce, motionZ, player, ModConfig.common.ledgeGrab.useExistingVelocity);
      ServerLedgeGrabEffects.run(player);
   }

   public static Vec3d getVectorForHorizontalRotation(float yaw) {
      float f = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
      float f1 = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
      float f2 = -MathHelper.cos(0.0F);
      float f3 = MathHelper.sin(0.0F);
      return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
   }
}
