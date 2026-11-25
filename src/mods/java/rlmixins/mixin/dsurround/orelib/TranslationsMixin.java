package rlmixins.mixin.dsurround.orelib;

import java.util.Map;
import org.orecruncher.lib.Translations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import rlmixins.handlers.DSurroundConfigHandler;
import rlmixins.wrapper.ITranslationsMixin;

@Mixin({Translations.class})
public abstract class TranslationsMixin implements ITranslationsMixin {
   @Shadow(
      remap = false
   )
   private Map<String, String> lookup;

   public TranslationsMixin() {
   }

   public void loadFromConfig(String lang) {
      Map<String, String> temp = DSurroundConfigHandler.getDSurroundChatMap(lang);
      if (temp != null) {
         this.lookup = temp;
      }

   }
}
