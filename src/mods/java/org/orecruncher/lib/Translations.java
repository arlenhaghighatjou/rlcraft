package org.orecruncher.lib;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.lang3.StringUtils;

public class Translations {
   public static final String DEFAULT_LANGUAGE = "en_us";
   private Map<String, String> lookup = new Object2ObjectOpenHashMap();

   public Translations() {
   }

   protected void merge(@Nonnull InputStream stream) throws IOException {
      FMLCommonHandler.instance().loadLanguage(this.lookup, stream);
   }

   private static List<String> getLanguageList(@Nullable String... lang) {
      List<String> result = new ArrayList();
      if (lang != null && lang.length > 0) {
         for(String s : lang) {
            if (result.contains(s)) {
               result.add(s);
            }
         }
      } else {
         String mcLang = Minecraft.getMinecraft().gameSettings.language;
         result.add("en_us");
         if (!result.contains(mcLang)) {
            result.add(mcLang);
         }
      }

      return result;
   }

   public void load(@Nonnull ResourceLocation loc) {
      String assetName = "/assets/" + loc.getResourceDomain() + "/lang/" + loc.getResourcePath() + ".lang";

      try {
         InputStream stream = Translations.class.getResourceAsStream(assetName);
         Throwable var4 = null;

         try {
            if (stream != null) {
               this.merge(stream);
            }
         } catch (Throwable var14) {
            var4 = var14;
            throw var14;
         } finally {
            if (stream != null) {
               if (var4 != null) {
                  try {
                     stream.close();
                  } catch (Throwable var13) {
                     var4.addSuppressed(var13);
                  }
               } else {
                  stream.close();
               }
            }

         }
      } catch (Throwable var16) {
         LibLog.log().error("Error merging language " + assetName, var16);
      }

   }

   public void load(@Nonnull String assetRoot, @Nullable String... lang) {
      for(String l : getLanguageList(lang)) {
         String assetName = StringUtils.appendIfMissing(assetRoot, "/", new CharSequence[0]) + l + ".lang";

         try {
            InputStream stream = Translations.class.getResourceAsStream(assetName);
            Throwable var8 = null;

            try {
               if (stream != null) {
                  this.merge(stream);
               }
            } catch (Throwable var18) {
               var8 = var18;
               throw var18;
            } finally {
               if (stream != null) {
                  if (var8 != null) {
                     try {
                        stream.close();
                     } catch (Throwable var17) {
                        var8.addSuppressed(var17);
                     }
                  } else {
                     stream.close();
                  }
               }

            }
         } catch (Throwable var20) {
            LibLog.log().error("Error merging language " + assetName, var20);
         }
      }

   }

   @Nonnull
   public String format(@Nonnull String translateKey, @Nullable Object... parameters) {
      String xlated = (String)this.lookup.get(translateKey);
      return xlated == null ? translateKey : String.format(xlated, parameters);
   }

   @Nonnull
   public String loadString(@Nonnull String translateKey) {
      String xlated = (String)this.lookup.get(translateKey);
      return xlated == null ? translateKey : xlated;
   }

   public void put(@Nonnull String key, @Nonnull String value) {
      this.lookup.put(key, value);
   }

   public void forAll(@Nonnull Predicate<Entry<String, String>> pred) {
      for(Entry<String, String> e : this.lookup.entrySet()) {
         pred.apply(e);
      }

   }

   public void transform(@Nonnull Function<Entry<String, String>, String> func) {
      Map<String, String> old = this.lookup;
      this.lookup = new Object2ObjectOpenHashMap();

      for(Entry<String, String> e : old.entrySet()) {
         this.lookup.put(e.getKey(), func.apply(e));
      }

   }
}
