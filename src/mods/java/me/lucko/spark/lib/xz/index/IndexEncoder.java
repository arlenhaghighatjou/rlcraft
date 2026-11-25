package me.lucko.spark.lib.xz.index;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import me.lucko.spark.lib.xz.XZIOException;
import me.lucko.spark.lib.xz.common.EncoderUtil;

public class IndexEncoder extends IndexBase {
   private final ArrayList<IndexRecord> records = new ArrayList();

   public IndexEncoder() {
      super(new XZIOException("XZ Stream or its Index has grown too big"));
   }

   public void add(long var1, long var3) throws XZIOException {
      super.add(var1, var3);
      this.records.add(new IndexRecord(var1, var3));
   }

   public void encode(OutputStream var1) throws IOException {
      CRC32 var2 = new CRC32();
      CheckedOutputStream var3 = new CheckedOutputStream(var1, var2);
      var3.write(0);
      EncoderUtil.encodeVLI(var3, this.recordCount);

      for(IndexRecord var5 : this.records) {
         EncoderUtil.encodeVLI(var3, var5.unpadded);
         EncoderUtil.encodeVLI(var3, var5.uncompressed);
      }

      for(int var7 = this.getIndexPaddingSize(); var7 > 0; --var7) {
         var3.write(0);
      }

      long var8 = var2.getValue();

      for(int var6 = 0; var6 < 4; ++var6) {
         var1.write((byte)((int)(var8 >>> var6 * 8)));
      }

   }
}
