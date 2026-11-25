package shadows.placebo;

import net.minecraft.block.Block;
import net.minecraft.util.text.translation.I18n;

public class Proxy {
   public Proxy() {
   }

   public String translate(String lang, Object... args) {
      return I18n.translateToLocalFormatted(lang, args);
   }

   public void useRenamedMapper(Block b, String path) {
   }

   public void useRenamedMapper(Block b, String path, String append) {
   }

   public void useRenamedMapper(Block b, String path, String append, String variant) {
   }
}
