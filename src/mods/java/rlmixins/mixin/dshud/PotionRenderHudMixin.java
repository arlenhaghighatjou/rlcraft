package rlmixins.mixin.dshud;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.orecruncher.lib.Localization;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
   targets = {"org.orecruncher.dshuds.hud.PotionHUD$PotionInfo"}
)
public abstract class PotionRenderHudMixin {
   private int amplifier = 0;

   public PotionRenderHudMixin() {
   }

   @Redirect(
      method = {"<init>"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/potion/Potion;shouldRenderInvText(Lnet/minecraft/potion/PotionEffect;)Z",
   remap = false
)
   )
   public boolean rlmixins_dsHudPotionRenderHud_PotionInfo_init_Redirect_0(Potion instance, PotionEffect effect) {
      this.amplifier = effect.getAmplifier();
      return instance.shouldRenderInvText(effect);
   }

   @Redirect(
      method = {"<init>"},
      at = @At(
   value = "INVOKE",
   target = "Lorg/orecruncher/lib/Localization;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;",
   ordinal = 0,
   remap = false
)
   )
   public String rlmixins_dsHudPotionRenderHud_PotionInfo_init_Redirect_1(String translateKey, Object[] parameters) {
      String text = Localization.format(translateKey, new Object[0]);
      if (this.amplifier > 3 && this.amplifier < 10) {
         text = text + " " + Localization.format("enchantment.level." + (this.amplifier + 1), new Object[0]);
      }

      this.amplifier = 0;
      return text;
   }
}
