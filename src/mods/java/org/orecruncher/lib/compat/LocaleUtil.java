package org.orecruncher.lib.compat;

import javax.annotation.Nonnull;
import net.minecraft.client.resources.Locale;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.ReflectedMethod;

@SideOnly(Side.CLIENT)
public final class LocaleUtil {
   private static final ReflectedMethod<String> tpk = new ReflectedMethod<String>(Locale.class, "translateKeyPrivate", "translateKeyPrivate", new Class[]{String.class});

   public LocaleUtil() {
   }

   public static String translateKeyPrivate(@Nonnull Locale locale, @Nonnull String key) {
      String result = tpk.invoke(locale, key);
      return result == null ? key : result;
   }
}
