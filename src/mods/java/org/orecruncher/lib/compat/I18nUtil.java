package org.orecruncher.lib.compat;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Locale;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.ReflectedField;

@SideOnly(Side.CLIENT)
public final class I18nUtil {
   private static final ReflectedField.ObjectField<I18n, Locale> i18nLocale = new ReflectedField.ObjectField<I18n, Locale>(I18n.class, "i18nLocale", "i18nLocale");

   public I18nUtil() {
   }

   public static Locale getLocale() {
      return i18nLocale.get((Object)null);
   }
}
