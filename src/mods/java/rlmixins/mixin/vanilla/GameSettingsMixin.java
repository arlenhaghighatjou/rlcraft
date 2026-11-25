package rlmixins.mixin.vanilla;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rlmixins.handlers.chunkanimator.ChunkAnimatorCooldownHandler;

@Mixin({GameSettings.class})
public abstract class GameSettingsMixin {
   public GameSettingsMixin() {
   }

   @Inject(
      method = {"setOptionValue"},
      at = {@At("HEAD")}
   )
   public void rlmixins_vanillaGameSettings_setOptionValue(Options settingsOption, int value, CallbackInfo ci) {
      if (settingsOption == Options.GRAPHICS || settingsOption == Options.AMBIENT_OCCLUSION || settingsOption == Options.USE_VBO) {
         ChunkAnimatorCooldownHandler.startCooldown();
      }

   }

   @Inject(
      method = {"setOptionFloatValue"},
      at = {@At("HEAD")}
   )
   public void rlmixins_vanillaGameSettings_setOptionFloatValue(Options settingsOption, float value, CallbackInfo ci) {
      if (settingsOption == Options.RENDER_DISTANCE) {
         ChunkAnimatorCooldownHandler.startCooldown();
      }

   }
}
