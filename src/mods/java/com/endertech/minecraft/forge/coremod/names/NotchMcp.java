package com.endertech.minecraft.forge.coremod.names;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class NotchMcp {
   static final Map<String, String> CLASSES = new HashMap();
   protected static final Map<String, NotchMcp.ClassContent> CLASS_CONTENT_MAP = new HashMap();
   protected static final String SOURCE_FILE = "notch-mcp.srg";
   protected static final String CLASS_PREFIX = "CL:";
   protected static final String FIELD_PREFIX = "FD:";
   protected static final String METHOD_PREFIX = "MD:";

   public NotchMcp() {
   }

   @Nonnull
   public static NotchMcp.ClassContent getContentOf(ClassName className) {
      return (NotchMcp.ClassContent)CLASS_CONTENT_MAP.getOrDefault(className.normal, NotchMcp.ClassContent.EMPTY);
   }

   public static void clearLists() {
      CLASSES.clear();
   }

   static {
      try {
         InputStreamReader reader = new InputStreamReader(NotchMcp.class.getResourceAsStream("notch-mcp.srg"));
         BufferedReader bf = new BufferedReader(reader);
         String line = null;

         while((line = bf.readLine()) != null) {
            String[] parts = line.split(" ");
            if (line.startsWith("CL:") && parts.length == 3) {
               String name = parts[2];
               CLASSES.put(name, parts[1]);
               CLASS_CONTENT_MAP.put(name, new NotchMcp.ClassContent());
            } else if (line.startsWith("FD:") && parts.length == 3) {
               String fullName = parts[2];
               String[] splitted = fullName.split("/");
               String name = splitted[splitted.length - 1];
               String className = fullName.substring(0, fullName.length() - name.length() - 1);
               splitted = parts[1].split("/");
               String obfuscated = splitted[splitted.length - 1];
               NotchMcp.ClassContent content = (NotchMcp.ClassContent)CLASS_CONTENT_MAP.get(className);
               if (content != null) {
                  content.FIELDS.put(name, obfuscated);
               }
            } else if (line.startsWith("MD:") && parts.length == 5) {
               String fullName = parts[3];
               String[] splitted = fullName.split("/");
               String name = splitted[splitted.length - 1];
               String className = fullName.substring(0, fullName.length() - name.length() - 1);
               splitted = parts[1].split("/");
               String obfuscated = splitted[splitted.length - 1];
               NotchMcp.ClassContent content = (NotchMcp.ClassContent)CLASS_CONTENT_MAP.get(className);
               if (content != null) {
                  content.METHODS.put(name, obfuscated);
               }
            }
         }
      } catch (IOException var10) {
         var10.printStackTrace();
      }

   }

   static class ClassContent {
      static final NotchMcp.ClassContent EMPTY = new NotchMcp.ClassContent();
      final Map<String, String> FIELDS = new HashMap();
      final Map<String, String> METHODS = new HashMap();

      ClassContent() {
      }
   }
}
