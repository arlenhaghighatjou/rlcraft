package snownee.kiwi.client;

import net.lax1dude.eaglercraft.v1_8.EaglerBidiReorder;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;

public class FontUtil {
   public FontUtil() {
   }

   public static List<String> drawSplitStringOverflow(FontRenderer fontRenderer, String str, int x, int y, int wrapWidth, int threshold, int textColor, boolean addShadow) {
      List<String> strs = fontRenderer.listFormattedStringToWidth(str, wrapWidth);
      return drawSplitStringOverflow(fontRenderer, strs, x, y, wrapWidth, threshold, textColor, addShadow);
   }

   public static List<String> drawSplitStringOverflow(FontRenderer fontRenderer, List<String> strs, int x, int y, int wrapWidth, int threshold, int textColor, boolean addShadow) {
      threshold = threshold + y;

      for(int i = 0; i < strs.size(); ++i) {
         y += fontRenderer.FONT_HEIGHT;
         if (y >= threshold && i < strs.size() - 1) {
            return strs.subList(i, strs.size());
         }

         renderStringAligned(fontRenderer, (String)strs.get(i), x, y, wrapWidth, textColor, addShadow);
      }

      return Lists.newArrayList();
   }

   private static String trimStringNewline(String text) {
      while(text != null && text.endsWith("\n")) {
         text = text.substring(0, text.length() - 1);
      }

      return text;
   }

   private static int renderStringAligned(FontRenderer fontRenderer, String text, int x, int y, int width, int color, boolean dropShadow) {
      if (fontRenderer.getBidiFlag()) {
         int i = fontRenderer.getStringWidth(bidiReorder(text));
         x = x + width - i;
      }

      return fontRenderer.drawString(text, (float)x, (float)y, color, dropShadow);
   }

   private static String bidiReorder(String text) {
      return EaglerBidiReorder.bidiReorder(text);
   }
}
