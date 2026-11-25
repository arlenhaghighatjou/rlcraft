package com.bloodnbonesgaming.lib.util.script;

import com.bloodnbonesgaming.lib.BNBGamingLib;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;

public class ScriptUtil {
   private static Map<String, ScriptConstructor> mainClassKeywords = new HashMap();

   public ScriptUtil() {
   }

   public static boolean readScript(VFile2 file, ScriptTarget mainObj, Map<String, ScriptConstructor> classKeywords) {
      return readScript(file, mainObj, classKeywords, (Map)null);
   }

   public static boolean readScript(VFile2 file, ScriptTarget mainObj, Map<String, ScriptConstructor> classKeywords, Map<String, Object> localVariables) {
      List<String> scriptLines = getScriptLines(file);
      return !scriptLines.isEmpty() ? (new ScriptParser(scriptLines, file.getPath(), mainObj, classKeywords, localVariables)).parseScript() : true;
   }

   public static ScriptConstructor getClassForKeyword(String keyword) {
      return mainClassKeywords.get(keyword);
   }

   public static List<String> getScriptLines(VFile2 file) {
      List<String> array = new ArrayList();
      String[] lines = file.getAllLines();
      if (lines == null) {
         BNBGamingLib.libInstance.getLog().error("Failed to load script file " + file.getName());
         return array;
      }

      for(int i = 0; i < lines.length; ++i) {
         array.add(lines[i].trim());
      }

      return array;
   }

   public static void addClassKeyword(String keyword, ScriptConstructor constructor) {
      if (keyword != null && constructor != null) {
         mainClassKeywords.put(keyword, constructor);
      }

   }

   public static void addClassKeywords(Map<String, ScriptConstructor> keywords) {
      if (keywords != null && !keywords.isEmpty()) {
         mainClassKeywords.putAll(keywords);
      }

   }
}
