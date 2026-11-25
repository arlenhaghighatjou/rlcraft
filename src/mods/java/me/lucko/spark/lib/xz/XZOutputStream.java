package me.lucko.spark.lib.xz;

import java.io.IOException;
import java.io.OutputStream;
import me.lucko.spark.lib.xz.check.Check;
import me.lucko.spark.lib.xz.common.EncoderUtil;
import me.lucko.spark.lib.xz.common.StreamFlags;
import me.lucko.spark.lib.xz.index.IndexEncoder;

public class XZOutputStream extends FinishableOutputStream {
   private final ArrayCache arrayCache;
   private OutputStream out;
   private final StreamFlags streamFlags;
   private final Check check;
   private final IndexEncoder index;
   private BlockOutputStream blockEncoder;
   private FilterEncoder[] filters;
   private boolean filtersSupportFlushing;
   private IOException exception;
   private boolean finished;
   private final byte[] tempBuf;

   public XZOutputStream(OutputStream var1, FilterOptions var2) throws IOException {
      this(var1, var2, 4);
   }

   public XZOutputStream(OutputStream var1, FilterOptions var2, ArrayCache var3) throws IOException {
      this(var1, var2, 4, var3);
   }

   public XZOutputStream(OutputStream var1, FilterOptions var2, int var3) throws IOException {
      this(var1, new FilterOptions[]{var2}, var3);
   }

   public XZOutputStream(OutputStream var1, FilterOptions var2, int var3, ArrayCache var4) throws IOException {
      this(var1, new FilterOptions[]{var2}, var3, var4);
   }

   public XZOutputStream(OutputStream var1, FilterOptions[] var2) throws IOException {
      this(var1, var2, 4);
   }

   public XZOutputStream(OutputStream var1, FilterOptions[] var2, ArrayCache var3) throws IOException {
      this(var1, var2, 4, var3);
   }

   public XZOutputStream(OutputStream var1, FilterOptions[] var2, int var3) throws IOException {
      this(var1, var2, var3, ArrayCache.getDefaultCache());
   }

   public XZOutputStream(OutputStream var1, FilterOptions[] var2, int var3, ArrayCache var4) throws IOException {
      this.streamFlags = new StreamFlags();
      this.index = new IndexEncoder();
      this.blockEncoder = null;
      this.exception = null;
      this.finished = false;
      this.tempBuf = new byte[1];
      this.arrayCache = var4;
      this.out = var1;
      this.updateFilters(var2);
      this.streamFlags.checkType = var3;
      this.check = Check.getInstance(var3);
      this.encodeStreamHeader();
   }

   public void updateFilters(FilterOptions var1) throws XZIOException {
      FilterOptions[] var2 = new FilterOptions[]{var1};
      this.updateFilters(var2);
   }

   public void updateFilters(FilterOptions[] var1) throws XZIOException {
      if (this.blockEncoder != null) {
         throw new UnsupportedOptionsException("Changing filter options in the middle of a XZ Block not implemented");
      } else if (var1.length >= 1 && var1.length <= 4) {
         this.filtersSupportFlushing = true;
         FilterEncoder[] var2 = new FilterEncoder[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            var2[var3] = var1[var3].getFilterEncoder();
            this.filtersSupportFlushing &= var2[var3].supportsFlushing();
         }

         RawCoder.validate(var2);
         this.filters = var2;
      } else {
         throw new UnsupportedOptionsException("XZ filter chain must be 1-4 filters");
      }
   }

   public void write(int var1) throws IOException {
      this.tempBuf[0] = (byte)var1;
      this.write(this.tempBuf, 0, 1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 + var3 >= 0 && var2 + var3 <= var1.length) {
         if (this.exception != null) {
            throw this.exception;
         } else if (this.finished) {
            throw new XZIOException("Stream finished or closed");
         } else {
            try {
               if (this.blockEncoder == null) {
                  this.blockEncoder = new BlockOutputStream(this.out, this.filters, this.check, this.arrayCache);
               }

               this.blockEncoder.write(var1, var2, var3);
            } catch (IOException var5) {
               this.exception = var5;
               throw var5;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void endBlock() throws IOException {
      if (this.exception != null) {
         throw this.exception;
      } else if (this.finished) {
         throw new XZIOException("Stream finished or closed");
      } else {
         if (this.blockEncoder != null) {
            try {
               this.blockEncoder.finish();
               this.index.add(this.blockEncoder.getUnpaddedSize(), this.blockEncoder.getUncompressedSize());
               this.blockEncoder = null;
            } catch (IOException var2) {
               this.exception = var2;
               throw var2;
            }
         }

      }
   }

   public void flush() throws IOException {
      if (this.exception != null) {
         throw this.exception;
      } else if (this.finished) {
         throw new XZIOException("Stream finished or closed");
      } else {
         try {
            if (this.blockEncoder != null) {
               if (this.filtersSupportFlushing) {
                  this.blockEncoder.flush();
               } else {
                  this.endBlock();
                  this.out.flush();
               }
            } else {
               this.out.flush();
            }

         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }
      }
   }

   public void finish() throws IOException {
      if (!this.finished) {
         this.endBlock();

         try {
            this.index.encode(this.out);
            this.encodeStreamFooter();
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }

         this.finished = true;
      }

   }

   public void close() throws IOException {
      if (this.out != null) {
         try {
            this.finish();
         } catch (IOException var2) {
            ;
         }

         try {
            this.out.close();
         } catch (IOException var3) {
            if (this.exception == null) {
               this.exception = var3;
            }
         }

         this.out = null;
      }

      if (this.exception != null) {
         throw this.exception;
      }
   }

   private void encodeStreamFlags(byte[] var1, int var2) {
      var1[var2] = 0;
      var1[var2 + 1] = (byte)this.streamFlags.checkType;
   }

   private void encodeStreamHeader() throws IOException {
      this.out.write(XZ.HEADER_MAGIC);
      byte[] var1 = new byte[2];
      this.encodeStreamFlags(var1, 0);
      this.out.write(var1);
      EncoderUtil.writeCRC32(this.out, var1);
   }

   private void encodeStreamFooter() throws IOException {
      byte[] var1 = new byte[6];
      long var2 = this.index.getIndexSize() / 4L - 1L;

      for(int var4 = 0; var4 < 4; ++var4) {
         var1[var4] = (byte)((int)(var2 >>> var4 * 8));
      }

      this.encodeStreamFlags(var1, 4);
      EncoderUtil.writeCRC32(this.out, var1);
      this.out.write(var1);
      this.out.write(XZ.FOOTER_MAGIC);
   }
}
