package com.lycanitesmobs.client.localisation;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.Utilities;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.io.IOUtils;

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
      inputStreamIn = FMLCommonHandler.instance().loadLanguage(this.map, inputStreamIn);
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
         String languageDir = String.format("lang/%s/", language);
         Path languageWalkPath = Utilities.getAssetPath(LycanitesMobs.modInfo.getClass(), LycanitesMobs.modInfo.modid, "lang/en_us/");

         try {
            Iterator<Path> languageDirIter = Files.walk(languageWalkPath).iterator();
            String languageSubdir = "";

            while(languageDirIter.hasNext()) {
               Path subdirPath = (Path)languageDirIter.next();
               if (!Files.isDirectory(subdirPath, new LinkOption[0])) {
                  try {
                     if (!subdirPath.toString().replace("\\", "/").contains(languageSubdir)) {
                        languageSubdir = "";
                     }

                     String languagePath = languageDir + languageSubdir + subdirPath.getFileName();
                     LycanitesMobs.logDebug("Language", "Reading translations from lang: " + languagePath + " Subdir Path:" + subdirPath.toString().replace("en_us", language));
                     ResourceLocation langLocation = new ResourceLocation(LycanitesMobs.modInfo.modid, languagePath);
                     getInstance().loadLocaleData(resourceManager.getResource(langLocation).getInputStream());
                  } catch (Exception var14) {
                     LycanitesMobs.logWarning("", "Error reading translations from lang: " + languageDir + languageSubdir + subdirPath.getFileName() + " Subdir: " + languageSubdir + " Subdir Path: " + subdirPath.toString().replace("en_us", language));
                  }

                  ++laodedLangFiles;
               } else {
                  languageSubdir = subdirPath.getName(subdirPath.getNameCount() - 1).toString().replace("\\", "/");
                  if (!languageSubdir.substring(languageSubdir.length() - 1).equals("/")) {
                     languageSubdir = languageSubdir + "/";
                  }

                  LycanitesMobs.logDebug("Language", "Setting Subdir: " + languageSubdir);
               }
            }
         } catch (Exception var15) {
            ;
         }
      }

      LycanitesMobs.logDebug("Localisation", laodedLangFiles + " Additional lang files loaded! Test translation: " + translate("lycanitesmobs.test"));
   }
}
