package com.bloodnbonesgaming.lib.util.script;

import com.bloodnbonesgaming.lib.BNBGamingLib;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptUtil {
   private static Map<String, Class> mainClassKeywords = new HashMap();

   public ScriptUtil() {
   }

   public static boolean readScript(File file, Object mainObj, Map<String, Class> classKeywords) {
      return readScript(file, mainObj, classKeywords, (Map)null);
   }

   public static boolean readScript(File file, Object mainObj, Map<String, Class> classKeywords, Map<String, Object> localVariables) {
      List<String> scriptLines = getScriptLines(file);
      return !scriptLines.isEmpty() ? (new ScriptParser(scriptLines, file.getAbsolutePath(), mainObj, classKeywords, localVariables)).parseScript() : true;
   }

   public static Class getClassForKeyword(String keyword) {
      return (Class)mainClassKeywords.get(keyword);
   }

   public static List<String> getScriptLines(File file) {
      List<String> array = new ArrayList();

      try {
         BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
         Throwable var3 = null;

         try {
            String line;
            try {
               while((line = r.readLine()) != null) {
                  array.add(line.trim());
               }
            } catch (Throwable var13) {
               var3 = var13;
               throw var13;
            }
         } finally {
            if (r != null) {
               if (var3 != null) {
                  try {
                     r.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  r.close();
               }
            }

         }
      } catch (IOException var15) {
         BNBGamingLib.libInstance.getLog().error("Failed to load script file " + file.getName());
      }

      return array;
   }

   public static void addClassKeyword(String keyword, Class clazz) {
      if (keyword != null && clazz != null) {
         mainClassKeywords.put(keyword, clazz);
      }

   }

   public static void addClassKeywords(Map<String, Class> keywords) {
      if (keywords != null && !keywords.isEmpty()) {
         mainClassKeywords.putAll(keywords);
      }

   }
}
