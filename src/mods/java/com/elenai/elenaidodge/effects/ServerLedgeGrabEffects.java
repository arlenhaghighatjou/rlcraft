package com.elenai.elenaidodge.effects;

import com.elenai.elenaidodge.ModConfig;
import com.elenai.elenaidodge.capability.ledgegrabcooldown.ILedgeGrabCooldown;
import com.elenai.elenaidodge.capability.ledgegrabcooldown.LedgeGrabCooldownProvider;
import com.elenai.elenaidodge.capability.ledgegrabs.ILedgeGrabs;
import com.elenai.elenaidodge.capability.ledgegrabs.LedgeGrabsProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;

public class ServerLedgeGrabEffects {
   public ServerLedgeGrabEffects() {
   }

   public static void run(EntityPlayerMP player) {
      ILedgeGrabs l = (ILedgeGrabs)player.getCapability(LedgeGrabsProvider.LEDGEGRABS_CAP, (EnumFacing)null);
      l.increase(1);
      player.fallDistance = 0.0F;
      ILedgeGrabCooldown lgc = (ILedgeGrabCooldown)player.getCapability(LedgeGrabCooldownProvider.LEDGEGRABCOOLDOWN_CAP, (EnumFacing)null);
      lgc.set(ModConfig.common.ledgeGrab.cooldown);
      player.getEntityWorld().playSound((EntityPlayer)null, player.getPosition(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 0.7F, 5.0F);
      if (!player.isCreative() && !player.isSpectator()) {
         player.getFoodStats().addExhaustion((float)ModConfig.common.ledgeGrab.exhaustion);
      }

   }
}
