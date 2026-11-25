package lumien.bloodmoon.asm;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MCPNames {
   private static Map<String, String> fields;
   private static Map<String, String> methods;

   public MCPNames() {
   }

   public static boolean mcp() {
      return LoadingPlugin.IN_MCP;
   }

   public static String field(String srgName) {
      return mcp() ? (String)fields.get(srgName) : srgName;
   }

   public static String method(String srgName) {
      return mcp() ? (String)methods.get(srgName) : srgName;
   }

   private static Map<String, String> readMappings(File file) {
      if (!file.isFile()) {
         throw new RuntimeException("Couldn't find MCP mappings.");
      } else {
         try {
            return (Map)Files.readLines(file, Charsets.UTF_8, new MCPNames.MCPFileParser());
         } catch (IOException var2) {
            throw new RuntimeException("Couldn't read SRG->MCP mappings", var2);
         }
      }
   }

   static {
      if (mcp()) {
         try {
            Class gradleClass = Class.forName("net.minecraftforge.gradle.GradleStartCommon");
            Field dirField = gradleClass.getDeclaredField("CSV_DIR");
            dirField.setAccessible(true);
            File mappingDir = (File)dirField.get((Object)null);
            fields = readMappings(new File(mappingDir, "fields.csv"));
            methods = readMappings(new File(mappingDir, "methods.csv"));
         } catch (Exception var3) {
            var3.printStackTrace();
            System.out.println("[BM] [ERROR] Error getting mappings from Gradlew SRG, falling back to mcp folder.");
            fields = readMappings(new File("./../mcp/fields.csv"));
            methods = readMappings(new File("./../mcp/methods.csv"));
         }
      } else {
         methods = null;
         fields = null;
      }

   }

   private static class MCPFileParser implements LineProcessor<Map<String, String>> {
      private static final Splitter splitter = Splitter.on(',').trimResults();
      private final Map<String, String> map;
      private boolean foundFirst;

      private MCPFileParser() {
         this.map = Maps.newHashMap();
      }

      public boolean processLine(String line) throws IOException {
         if (!this.foundFirst) {
            this.foundFirst = true;
            return true;
         } else {
            Iterator<String> splitted = splitter.split(line).iterator();

            try {
               String srg = (String)splitted.next();
               String mcp = (String)splitted.next();
               if (!this.map.containsKey(srg)) {
                  this.map.put(srg, mcp);
               }

               return true;
            } catch (NoSuchElementException var5) {
               throw new IOException("Invalid Mappings file!", var5);
            }
         }
      }

      public Map<String, String> getResult() {
         return ImmutableMap.copyOf(this.map);
      }
   }
}
