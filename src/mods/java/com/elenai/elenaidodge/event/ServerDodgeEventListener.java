package com.elenai.elenaidodge.event;

import com.elenai.elenaidodge.ModConfig;
import com.elenai.elenaidodge.api.DodgeEvent;
import com.elenai.elenaidodge.capability.airborne.AirborneProvider;
import com.elenai.elenaidodge.capability.airborne.IAirborne;
import com.elenai.elenaidodge.capability.enabled.EnabledProvider;
import com.elenai.elenaidodge.capability.enabled.IEnabled;
import com.elenai.elenaidodge.init.PotionInit;
import com.elenai.elenaidodge.network.PacketHandler;
import com.elenai.elenaidodge.network.message.CUpdateCooldownMessage;
import com.elenai.elenaidodge.util.Utils;
import com.elenai.elenaidodge.util.WeightTier;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerDodgeEventListener {
   public ServerDodgeEventListener() {
   }

   @SubscribeEvent
   public void onServerDodge(DodgeEvent.ServerDodgeEvent event) {
      EntityPlayer player = event.getPlayer();
      if (ModConfig.common.weights.enable) {
         WeightTier weightTier = Utils.getWeightTier(player);
         event.setForce(weightTier.getForce());
         PacketHandler.instance.sendTo(new CUpdateCooldownMessage(Utils.getWeightTier(player).getCooldown()), (EntityPlayerMP)player);
      }

      IAirborne a = (IAirborne)player.getCapability(AirborneProvider.AIRBORNE_CAP, (EnumFacing)null);
      if (!player.onGround && !ModConfig.common.balance.enableWhilstAirborne && !player.isPotionActive(PotionInit.SKYSTRIDE_EFFECT) && !Utils.hasAirborneGamestage(player) && !a.isEnabled()) {
         event.setCanceled(true);
      }

      if (ModConfig.common.gamestages.enable && !Utils.hasGamestage(player)) {
         event.setCanceled(true);
      }

      IEnabled e = (IEnabled)player.getCapability(EnabledProvider.ENABLED_CAP, (EnumFacing)null);
      if (!e.isEnabled()) {
         event.setCanceled(true);
      }

      if (player.isPotionActive(PotionInit.SLUGGISH_EFFECT)) {
         event.setCanceled(true);
      }

      if (ModConfig.common.balance.requiresPotion && !player.isPotionActive(PotionInit.CAN_DODGE_EFFECT)) {
         event.setCanceled(true);
      }

      if (!Utils.dodgeTraitUnlocked(player)) {
         event.setCanceled(true);
      }

      if (player.getFoodStats().getFoodLevel() <= ModConfig.common.balance.hunger) {
         event.setCanceled(true);
      }

      if (player.isSneaking() && !ModConfig.common.balance.enableWhilstSneaking) {
         event.setCanceled(true);
      }

      if (player.isActiveItemStackBlocking() && !ModConfig.common.balance.enableWhilstBlocking) {
         event.setCanceled(true);
      }

      if (player.isRiding()) {
         event.setCanceled(true);
      }

      if (!player.isCreative() && !player.isSpectator() && event.getCooldown() > 0) {
         event.setCanceled(true);
      }

      for(String i : ModConfig.common.balance.potions) {
         player.getActivePotionEffects().forEach((p) -> {
            if (p.getPotion().getRegistryName().equals(new ResourceLocation(i))) {
               event.setCanceled(true);
            }

         });
      }

      if (player.isPotionActive(MobEffects.SLOWNESS)) {
         event.setForce(event.getForce() / (double)(player.getActivePotionEffect(MobEffects.SLOWNESS).getAmplifier() + 2));
         if (player.getActivePotionEffect(MobEffects.SLOWNESS).getAmplifier() > 5) {
            event.setCanceled(true);
         }
      }

      if (player.dimension == -1 && ModConfig.common.misc.nether) {
         event.setForce(event.getForce() * 1.25D);
      }

      if (player.dimension == 1 && ModConfig.common.misc.end) {
         event.setForce(event.getForce() / 1.25D);
      }

      if (player.isPotionActive(PotionInit.FORCEFUL_EFFECT)) {
         event.setForce(event.getForce() + ((double)player.getActivePotionEffect(PotionInit.FORCEFUL_EFFECT).getAmplifier() + 0.3D) / 2.0D);
      }

      if (player.isPotionActive(PotionInit.FEEBLE_EFFECT)) {
         event.setForce(event.getForce() / (double)(player.getActivePotionEffect(PotionInit.FEEBLE_EFFECT).getAmplifier() + 2));
      }

      if (event.getForce() <= 0.0D) {
         event.setCanceled(true);
      }

   }
}
