package rlmixins.mixin.dsurround;

import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import org.orecruncher.dsurround.client.handlers.effects.EntityChatEffect;
import org.orecruncher.lib.Translations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rlmixins.handlers.DSurroundConfigHandler;
import rlmixins.wrapper.ITranslationsMixin;

@Mixin({EntityChatEffect.class})
public abstract class EntityChatEffectMixin {
   public EntityChatEffectMixin() {
   }

   @Shadow(
      remap = false
   )
   static void setTimers(@Nonnull String entity, int base, int random) {
   }

   @Redirect(
      method = {"<clinit>"},
      at = @At(
   value = "INVOKE",
   target = "Lorg/orecruncher/lib/Translations;load(Ljava/lang/String;[Ljava/lang/String;)V"
),
      remap = false
   )
   private static void rlmixins_dSurroundEntityChatEffect_clInit_Load(Translations instance, String t, String[] assetName) {
      ((ITranslationsMixin)instance).loadFromConfig(Minecraft.getMinecraft().gameSettings.language);
   }

   @Inject(
      method = {"<clinit>"},
      at = {@At("RETURN")},
      remap = false
   )
   private static void rlmixins_dSurroundENtityChatEffect_clInit_Timers(CallbackInfo ci) {
      Map<String, int[]> temp = DSurroundConfigHandler.getDSurroundChatTimeMap();
      if (temp != null) {
         for(Entry<String, int[]> ent : temp.entrySet()) {
            setTimers((String)ent.getKey(), ((int[])ent.getValue())[0], ((int[])ent.getValue())[1]);
         }
      }

   }
}
