package com.lycanitesmobs.client.localisation;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.Utilities;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.lax1dude.eaglercraft.v1_8.IOUtils;

public class LanguageManager {
   public static LanguageManager INSTANCE;
   protected static final Splitter SPLITTER = Splitter.on('=').limit(2);
   protected static final Pattern PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   protected Map<String, String> map = new HashMap();

   public LanguageManager() {
   }

   public static LanguageManager getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new LanguageManager();
      }

      return INSTANCE;
   }

   public static String translate(String key) {
      return I18n.translateToLocal(key);
   }

   public void loadLocaleData(InputStream inputStreamIn) throws IOException {
      if (inputStreamIn != null) {
         for(String s : IOUtils.readLines(inputStreamIn, StandardCharsets.UTF_8)) {
            if (!s.isEmpty() && s.charAt(0) != '#') {
               String[] splitters = (String[])Iterables.toArray(SPLITTER.split(s), String.class);
               if (splitters != null && splitters.length == 2) {
                  String s1 = splitters[0];
                  String s2 = PATTERN.matcher(splitters[1]).replaceAll("%$1s");
                  this.map.put(s1, s2);
               }
            }
         }

      }
   }

   public void loadLanguage(String mainLanguage, IResourceManager resourceManager) {
      LycanitesMobs.logDebug("Localisation", "Loading additional lang files...");
      List<String> languageList = Lists.newArrayList(new String[]{mainLanguage});
      if (!"en_us".equals(mainLanguage)) {
         languageList.add(mainLanguage);
      }

      int laodedLangFiles = 0;

      for(String language : languageList) {
         String languageDir = "lang/" + language + "/";
         String languageWalkPath = Utilities.getAssetPath(LycanitesMobs.modInfo.getClass(), LycanitesMobs.modInfo.modid, "lang/en_us/");

         try {
            List<String> languageFiles = EagRuntime.listResources(languageWalkPath);

            for(int i = 0, l = languageFiles.size(); i < l; ++i) {
               String languageFile = languageFiles.get(i).substring(languageWalkPath.length());
               try {
                  String languagePath = languageDir + languageFile;
                  LycanitesMobs.logDebug("Language", "Reading translations from lang: " + languagePath);
                  ResourceLocation langLocation = new ResourceLocation(LycanitesMobs.modInfo.modid, languagePath);
                  getInstance().loadLocaleData(resourceManager.getResource(langLocation).getInputStream());
               } catch (Exception var14) {
                  LycanitesMobs.logWarning("", "Error reading translations from lang: " + languageDir + languageFile);
               }

               ++laodedLangFiles;
            }
         } catch (Exception var15) {
            ;
         }
      }

      LycanitesMobs.logDebug("Localisation", laodedLangFiles + " Additional lang files loaded! Test translation: " + translate("lycanitesmobs.test"));
   }
}
