package com.endertech.common;

import java.nio.file.Path;
import java.util.StringJoiner;

public final class CommonPath {
   private static final String RESERVED_CHARS = "<>:\"|?*";
   public static final String NAME_EXT_DELIMITER = ".";

   public CommonPath() {
   }

   public static String getFileNameOnly(Path path) {
      String fileName = path != null ? String.valueOf(path.getFileName()) : "";
      int i = fileName.lastIndexOf(".");
      return i >= 0 ? fileName.substring(0, i) : fileName;
   }

   public static String getFileExtension(Path path) {
      String fileExt = path != null ? String.valueOf(path.getFileName()) : "";
      int i = fileExt.lastIndexOf(".");
      return i >= 0 ? fileExt.substring(i + 1) : fileExt;
   }

   public static String replaceReservedCharsWith(String string, String replacement) {
      StringJoiner regex = new StringJoiner("/", "[", "]");

      for(int i = 0; i < "<>:\"|?*".length(); ++i) {
         regex.add(String.valueOf("<>:\"|?*".charAt(i)));
      }

      return string.replaceAll(regex.toString(), replacement);
   }

   public static String removeReservedChars(String string) {
      return replaceReservedCharsWith(string, "");
   }
}
