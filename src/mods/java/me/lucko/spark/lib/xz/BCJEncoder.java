package me.lucko.spark.lib.xz;

class BCJEncoder extends BCJCoder implements FilterEncoder {
   private final BCJOptions options;
   private final long filterID;
   private final byte[] props;

   BCJEncoder(BCJOptions var1, long var2) {
      assert isBCJFilterID(var2);

      int var4 = var1.getStartOffset();
      if (var4 == 0) {
         this.props = new byte[0];
      } else {
         this.props = new byte[4];

         for(int var5 = 0; var5 < 4; ++var5) {
            this.props[var5] = (byte)(var4 >>> var5 * 8);
         }
      }

      this.filterID = var2;
      this.options = (BCJOptions)var1.clone();
   }

   public long getFilterID() {
      return this.filterID;
   }

   public byte[] getFilterProps() {
      return this.props;
   }

   public boolean supportsFlushing() {
      return false;
   }

   public FinishableOutputStream getOutputStream(FinishableOutputStream var1, ArrayCache var2) {
      return this.options.getOutputStream(var1, var2);
   }
}
