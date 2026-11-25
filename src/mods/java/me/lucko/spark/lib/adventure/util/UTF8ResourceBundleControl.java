package me.lucko.spark.lib.adventure.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import org.jetbrains.annotations.NotNull;

public final class UTF8ResourceBundleControl extends Control {
   private static final UTF8ResourceBundleControl INSTANCE = new UTF8ResourceBundleControl();

   public UTF8ResourceBundleControl() {
   }

   @NotNull
   public static Control get() {
      return INSTANCE;
   }

   public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
      if (format.equals("java.properties")) {
         String bundle = this.toBundleName(baseName, locale);
         String resource = this.toResourceName(bundle, "properties");

         InputStream is;
         try {
            is = (InputStream)AccessController.doPrivileged(() -> {
               if (reload) {
                  URL url = loader.getResource(resource);
                  if (url != null) {
                     URLConnection connection = url.openConnection();
                     if (connection != null) {
                        connection.setUseCaches(false);
                        return connection.getInputStream();
                     }
                  }

                  return null;
               } else {
                  return loader.getResourceAsStream(resource);
               }
            });
         } catch (PrivilegedActionException var14) {
            throw (IOException)var14.getException();
         }

         if (is != null) {
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);

            PropertyResourceBundle var10;
            try {
               var10 = new PropertyResourceBundle(isr);
            } catch (Throwable var13) {
               try {
                  isr.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }

               throw var13;
            }

            isr.close();
            return var10;
         } else {
            return null;
         }
      } else {
         return super.newBundle(baseName, locale, format, loader, reload);
      }
   }
}
