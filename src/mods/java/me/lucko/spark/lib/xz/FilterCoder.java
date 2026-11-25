package me.lucko.spark.lib.xz;

interface FilterCoder {
   boolean changesSize();

   boolean nonLastOK();

   boolean lastOK();
}
