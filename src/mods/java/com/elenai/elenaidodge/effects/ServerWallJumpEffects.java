package com.elenai.elenaidodge.effects;

import com.elenai.elenaidodge.ModConfig;
import com.elenai.elenaidodge.capability.walljumpcooldown.IWallJumpCooldown;
import com.elenai.elenaidodge.capability.walljumpcooldown.WallJumpCooldownProvider;
import com.elenai.elenaidodge.capability.walljumps.IWallJumps;
import com.elenai.elenaidodge.capability.walljumps.WallJumpsProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;

public class ServerWallJumpEffects {
   public ServerWallJumpEffects() {
   }

   public static void run(EntityPlayerMP player) {
      player.fallDistance = 0.0F;
      IWallJumps w = (IWallJumps)player.getCapability(WallJumpsProvider.WALLJUMPS_CAP, (EnumFacing)null);
      w.increase(1);
      IWallJumpCooldown wjc = (IWallJumpCooldown)player.getCapability(WallJumpCooldownProvider.WALLJUMPCOOLDOWN_CAP, (EnumFacing)null);
      wjc.set(ModConfig.common.wallJump.cooldown);
      player.getEntityWorld().playSound((EntityPlayer)null, player.getPosition(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 0.7F, 5.0F);
      if (!player.isCreative() && !player.isSpectator()) {
         player.getFoodStats().addExhaustion((float)ModConfig.common.wallJump.exhaustion);
      }

   }
}
