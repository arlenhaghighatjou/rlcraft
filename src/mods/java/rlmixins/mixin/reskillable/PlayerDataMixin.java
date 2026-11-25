package rlmixins.mixin.reskillable;

import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.RequirementHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rlmixins.RLMixins;

@Mixin({PlayerData.class})
public abstract class PlayerDataMixin {
   public PlayerDataMixin() {
   }

   @Inject(
      method = {"matchStats"},
      at = {@At("RETURN")},
      cancellable = true,
      remap = false
   )
   public void rlmixins_reskillablePlayerData_matchStats(RequirementHolder holder, CallbackInfoReturnable<Boolean> cir) {
      if (!((Boolean)cir.getReturnValue()).booleanValue() && ((RequirementCacheInvoker)((RequirementCacheInvoker)((PlayerData)this).getRequirementCache())).invokeGetPlayer() == null) {
         RLMixins.LOGGER.log(Level.WARN, "RLMixins: Detected player " + ((EntityPlayer)((PlayerData)this).playerWR.get()).getName() + " missing from worlds for Reskillable check, passing check. This should only happen when a player is returning from the End, if you believe it is in error, please report it.");
         cir.setReturnValue(Boolean.valueOf(true));
      }

   }
}
