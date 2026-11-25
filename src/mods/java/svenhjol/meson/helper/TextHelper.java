package svenhjol.meson.helper;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.text.TextFormatting;

public class TextHelper {
   public static Map<Integer, Integer> dyeTextMap = new HashMap();

   public TextHelper() {
   }

   public static TextFormatting getTextFormattingByDyeDamage(int dyeDamage) {
      return TextFormatting.fromColorIndex(((Integer)dyeTextMap.get(Integer.valueOf(dyeDamage))).intValue());
   }

   static {
      dyeTextMap.put(Integer.valueOf(0), Integer.valueOf(15));
      dyeTextMap.put(Integer.valueOf(1), Integer.valueOf(12));
      dyeTextMap.put(Integer.valueOf(2), Integer.valueOf(2));
      dyeTextMap.put(Integer.valueOf(3), Integer.valueOf(4));
      dyeTextMap.put(Integer.valueOf(4), Integer.valueOf(1));
      dyeTextMap.put(Integer.valueOf(5), Integer.valueOf(5));
      dyeTextMap.put(Integer.valueOf(6), Integer.valueOf(11));
      dyeTextMap.put(Integer.valueOf(7), Integer.valueOf(7));
      dyeTextMap.put(Integer.valueOf(8), Integer.valueOf(8));
      dyeTextMap.put(Integer.valueOf(9), Integer.valueOf(13));
      dyeTextMap.put(Integer.valueOf(10), Integer.valueOf(10));
      dyeTextMap.put(Integer.valueOf(11), Integer.valueOf(14));
      dyeTextMap.put(Integer.valueOf(12), Integer.valueOf(9));
      dyeTextMap.put(Integer.valueOf(13), Integer.valueOf(13));
      dyeTextMap.put(Integer.valueOf(14), Integer.valueOf(6));
      dyeTextMap.put(Integer.valueOf(15), Integer.valueOf(15));
   }
}
