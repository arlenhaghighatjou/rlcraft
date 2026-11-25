package me.lucko.spark.lib.xz;

import me.lucko.spark.lib.xz.lzma.LZMAEncoder;

class LZMA2Encoder extends LZMA2Coder implements FilterEncoder {
   private final LZMA2Options options;
   private final byte[] props = new byte[1];

   LZMA2Encoder(LZMA2Options var1) {
      if (var1.getPresetDict() != null) {
         throw new IllegalArgumentException("XZ doesn't support a preset dictionary for now");
      } else {
         if (var1.getMode() == 0) {
            this.props[0] = 0;
         } else {
            int var2 = Math.max(var1.getDictSize(), 4096);
            this.props[0] = (byte)(LZMAEncoder.getDistSlot(var2 - 1) - 23);
         }

         this.options = (LZMA2Options)var1.clone();
      }
   }

   public long getFilterID() {
      return 33L;
   }

   public byte[] getFilterProps() {
      return this.props;
   }

   public boolean supportsFlushing() {
      return true;
   }

   public FinishableOutputStream getOutputStream(FinishableOutputStream var1, ArrayCache var2) {
      return this.options.getOutputStream(var1, var2);
   }
}
