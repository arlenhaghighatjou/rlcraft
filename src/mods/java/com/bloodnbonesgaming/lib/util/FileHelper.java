package com.bloodnbonesgaming.lib.util;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;

public class FileHelper {
   public FileHelper() {
   }

   public static void writeText(String fileName, String text) {
      writeText(new VFile2(fileName), text);
   }

   public static void writeText(VFile2 file, String text) {
      file.setAllChars(text);
   }
}
