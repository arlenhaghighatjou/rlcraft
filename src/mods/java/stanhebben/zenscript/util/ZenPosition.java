package stanhebben.zenscript.util;

import stanhebben.zenscript.ZenParsedFile;

public class ZenPosition {
   private final ZenParsedFile file;
   private final int line;
   private final int offset;
   private final String fileNameFallback;

   public ZenPosition(ZenParsedFile file, int line, int offset, String fileNameFallback) {
      if (file != null && line <= 0) {
         throw new IllegalArgumentException("Line must be positive");
      } else {
         this.file = file;
         this.line = line;
         this.offset = offset;
         this.fileNameFallback = fileNameFallback;
      }
   }

   public ZenParsedFile getFile() {
      return this.file;
   }

   public String getFileName() {
      return this.file == null ? (this.fileNameFallback == null ? "?" : this.fileNameFallback) : this.file.getFileName();
   }

   public int getLine() {
      return this.line;
   }

   public int getLineOffset() {
      return this.offset;
   }

   public String toString() {
      return this.getFileName() + ":" + this.line;
   }
}
