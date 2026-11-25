package net.mcft.copy.backpacks.misc.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Predicate;
import net.mcft.copy.backpacks.WearableBackpacks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public final class IntermodUtils {
   private static final int DEFAULT_ENCHANTMENT_COLOR = -8372020;
   private static boolean getRuneColorCached = false;
   private static Method setTargetStackMethod = null;
   private static Method getColorMethod = null;

   private IntermodUtils() {
   }

   public static int getRuneColor(ItemStack stack) {
      if (!getRuneColorCached) {
         if (Loader.isModLoaded("quark")) {
            try {
               Class<?> clazz = Class.forName("vazkii.quark.misc.feature.ColorRunes");
               setTargetStackMethod = clazz.getMethod("setTargetStack", ItemStack.class);
               getColorMethod = (Method)Arrays.stream(clazz.getMethods()).filter((m) -> {
                  return "getColor".equals(m.getName());
               }).findAny().orElse((Object)null);
            } catch (NoSuchMethodException | ClassNotFoundException var3) {
               WearableBackpacks.LOG.error("Error while fetching Quark ColorRunes methods", var3);
            }
         }

         getRuneColorCached = true;
      }

      if (setTargetStackMethod != null && getColorMethod != null) {
         try {
            setTargetStackMethod.invoke((Object)null, stack);
            return getColorMethod.getParameterCount() == 0 ? ((Integer)getColorMethod.invoke((Object)null)).intValue() : ((Integer)getColorMethod.invoke((Object)null, -8372020)).intValue();
         } catch (Exception var2) {
            WearableBackpacks.LOG.error("Error while invoking Quark ColorRunes methods", var2);
            setTargetStackMethod = null;
            getColorMethod = null;
            return -8372020;
         }
      } else {
         return -8372020;
      }
   }
}
