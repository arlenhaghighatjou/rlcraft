package org.orecruncher.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Locale;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import org.orecruncher.LibBase;
import org.orecruncher.lib.compat.I18nUtil;
import org.orecruncher.lib.compat.LocaleUtil;

public final class Localization {
   private static Localization.Local impl;

   public Localization() {
   }

   public static void initialize(@Nonnull Side side, @Nonnull String modId) {
      if (impl == null) {
         if (side == Side.SERVER) {
            impl = new Localization.ServerImpl();
         } else {
            impl = new Localization.ClientImpl();
         }
      }

      ResourceLocation loc = new ResourceLocation(modId, "en_us");
      LibBase.log().info("Loading language resources [%s]", loc.toString());
      impl.loadResources(loc);
   }

   @Nonnull
   public static String format(@Nonnull String translateKey, @Nullable Object... parameters) {
      return impl.format(translateKey, parameters);
   }

   public static String loadString(@Nonnull String translateKey) {
      return impl.loadString(translateKey);
   }

   private static class ClientImpl extends Localization.Local {
      private ClientImpl() {
      }

      public String format(String translateKey, Object... parameters) {
         return I18n.format(translateKey, parameters);
      }

      public String loadString(String translateKey) {
         Locale locale = I18nUtil.getLocale();
         return LocaleUtil.translateKeyPrivate(locale, translateKey);
      }
   }

   private abstract static class Local {
      private Local() {
      }

      public abstract String format(String var1, Object... var2);

      public abstract String loadString(String var1);

      public void loadResources(@Nonnull ResourceLocation loc) {
      }
   }

   private static class ServerImpl extends Localization.Local {
      private final Translations xlate;

      private ServerImpl() {
         this.xlate = new Translations();
      }

      public String format(String translateKey, Object... parameters) {
         return this.xlate.format(translateKey, parameters);
      }

      public String loadString(String translateKey) {
         return this.xlate.loadString(translateKey);
      }

      public void loadResources(@Nonnull ResourceLocation loc) {
         this.xlate.load(loc);
      }
   }
}
