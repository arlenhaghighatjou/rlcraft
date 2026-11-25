package me.lucko.spark.lib.xz;

class DeltaEncoder extends DeltaCoder implements FilterEncoder {
   private final DeltaOptions options;
   private final byte[] props = new byte[1];

   DeltaEncoder(DeltaOptions var1) {
      this.props[0] = (byte)(var1.getDistance() - 1);
      this.options = (DeltaOptions)var1.clone();
   }

   public long getFilterID() {
      return 3L;
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
