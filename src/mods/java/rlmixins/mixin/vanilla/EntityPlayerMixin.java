package rlmixins.mixin.vanilla;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({EntityPlayer.class})
public abstract class EntityPlayerMixin {
   public EntityPlayerMixin() {
   }

   @ModifyConstant(
      method = {"getEyeHeight"},
      constant = {@Constant(
   floatValue = 0.08F
)}
   )
   public float rlmixins_vanillaEntityPlayer_getEyeHeight(float constant) {
      return ((EntityPlayer)this).eyeHeight / ((EntityPlayer)this).getDefaultEyeHeight() * 0.3F;
   }
}
