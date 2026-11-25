package me.lucko.spark.lib.xz;

abstract class DeltaCoder implements FilterCoder {
   public static final long FILTER_ID = 3L;

   DeltaCoder() {
   }

   public boolean changesSize() {
      return false;
   }

   public boolean nonLastOK() {
      return true;
   }

   public boolean lastOK() {
      return false;
   }
}
