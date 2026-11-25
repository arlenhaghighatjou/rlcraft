package net.ilexiconn.llibrary.client.lang;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.ilexiconn.llibrary.server.core.plugin.LLibraryPlugin;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum LanguageHandler {
   INSTANCE;

   private Map<String, Map<String, String>> localizations = new HashMap();

   private LanguageHandler() {
   }

   public RemoteLanguageContainer loadRemoteLocalization(String modId) throws Exception {
      InputStream in = LanguageHandler.class.getResourceAsStream("/assets/" + modId.toLowerCase() + "/lang.json");
      Throwable var3 = null;

      RemoteLanguageContainer var4;
      try {
         if (in == null) {
            return null;
         }

         var4 = (RemoteLanguageContainer)(new Gson()).fromJson(new InputStreamReader(in), RemoteLanguageContainer.class);
      } catch (Throwable var14) {
         var3 = var14;
         throw var14;
      } finally {
         if (in != null) {
            if (var3 != null) {
               try {
                  in.close();
               } catch (Throwable var13) {
                  var3.addSuppressed(var13);
               }
            } else {
               in.close();
            }
         }

      }

      return var4;
   }

   public void load() {
      File cacheDir = new File("llibrary/lang");
      if (!cacheDir.exists()) {
         cacheDir.mkdirs();
      }

      for(File child : cacheDir.listFiles()) {
         if (child.isFile()) {
            try {
               FileInputStream cachedStream = new FileInputStream(child);
               Throwable var7 = null;

               try {
                  Map<String, String> lang = LanguageMap.parseLangFile(cachedStream);
                  this.localizations.put(child.getName().substring(0, child.getName().length() - ".lang".length()), lang);
               } catch (Throwable var37) {
                  var7 = var37;
                  throw var37;
               } finally {
                  if (cachedStream != null) {
                     if (var7 != null) {
                        try {
                           cachedStream.close();
                        } catch (Throwable var36) {
                           var7.addSuppressed(var36);
                        }
                     } else {
                        cachedStream.close();
                     }
                  }

               }
            } catch (Exception var43) {
               LLibraryPlugin.LOGGER.error("An exception occurred while loading {} from cache.", child.getName(), var43);
            }
         }
      }

      for(ModContainer mod : Loader.instance().getModList()) {
         String modId = mod.getModId();

         try {
            RemoteLanguageContainer container = this.loadRemoteLocalization(modId);
            if (container != null) {
               for(RemoteLanguageContainer.LangContainer language : container.languages) {
                  Map<String, String> lang = LanguageMap.parseLangFile((new URL(language.downloadURL)).openStream());
                  String locale = language.locale;
                  if (this.localizations.containsKey(locale)) {
                     lang.putAll((Map)this.localizations.get(locale));
                  }

                  this.localizations.put(locale, lang);
               }
            }
         } catch (Exception var41) {
            LLibraryPlugin.LOGGER.error("An exception occurred while loading remote lang container for {}", modId, var41);
         }
      }

      for(Entry<String, Map<String, String>> entry : this.localizations.entrySet()) {
         String language = (String)entry.getKey();
         File cache = new File(cacheDir, language + ".lang");

         try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cache), Charsets.UTF_8));
            Throwable var55 = null;

            try {
               for(Entry<String, String> langEntry : ((Map)entry.getValue()).entrySet()) {
                  writer.append((CharSequence)langEntry.getKey()).append("=").append((CharSequence)langEntry.getValue()).append("\n");
               }
            } catch (Throwable var38) {
               var55 = var38;
               throw var38;
            } finally {
               if (writer != null) {
                  if (var55 != null) {
                     try {
                        writer.close();
                     } catch (Throwable var35) {
                        var55.addSuppressed(var35);
                     }
                  } else {
                     writer.close();
                  }
               }

            }
         } catch (Exception var40) {
            LLibraryPlugin.LOGGER.error("An exception occurred while saving cache for {}", language);
         }
      }

   }

   public void addRemoteLocalizations(String language, Map<String, String> properties) {
      Map<String, String> localizationsForLang = (Map)this.localizations.get(language);
      if (localizationsForLang != null) {
         properties.putAll(localizationsForLang);
      }

   }
}
