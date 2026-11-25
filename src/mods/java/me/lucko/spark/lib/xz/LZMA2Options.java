package me.lucko.spark.lib.xz;

import java.io.IOException;
import java.io.InputStream;

public class LZMA2Options extends FilterOptions {
   public static final int PRESET_MIN = 0;
   public static final int PRESET_MAX = 9;
   public static final int PRESET_DEFAULT = 6;
   public static final int DICT_SIZE_MIN = 4096;
   public static final int DICT_SIZE_MAX = 805306368;
   public static final int DICT_SIZE_DEFAULT = 8388608;
   public static final int LC_LP_MAX = 4;
   public static final int LC_DEFAULT = 3;
   public static final int LP_DEFAULT = 0;
   public static final int PB_MAX = 4;
   public static final int PB_DEFAULT = 2;
   public static final int MODE_UNCOMPRESSED = 0;
   public static final int MODE_FAST = 1;
   public static final int MODE_NORMAL = 2;
   public static final int NICE_LEN_MIN = 8;
   public static final int NICE_LEN_MAX = 273;
   public static final int MF_HC4 = 4;
   public static final int MF_BT4 = 20;
   private static final int[] presetToDictSize = new int[]{262144, 1048576, 2097152, 4194304, 4194304, 8388608, 8388608, 16777216, 33554432, 67108864};
   private static final int[] presetToDepthLimit = new int[]{4, 8, 24, 48};
   private int dictSize;
   private byte[] presetDict = null;
   private int lc;
   private int lp;
   private int pb;
   private int mode;
   private int niceLen;
   private int mf;
   private int depthLimit;

   public LZMA2Options() {
      try {
         this.setPreset(6);
      } catch (UnsupportedOptionsException var2) {
         assert false;

         throw new RuntimeException();
      }
   }

   public LZMA2Options(int var1) throws UnsupportedOptionsException {
      this.setPreset(var1);
   }

   public LZMA2Options(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) throws UnsupportedOptionsException {
      this.setDictSize(var1);
      this.setLcLp(var2, var3);
      this.setPb(var4);
      this.setMode(var5);
      this.setNiceLen(var6);
      this.setMatchFinder(var7);
      this.setDepthLimit(var8);
   }

   public void setPreset(int var1) throws UnsupportedOptionsException {
      if (var1 >= 0 && var1 <= 9) {
         this.lc = 3;
         this.lp = 0;
         this.pb = 2;
         this.dictSize = presetToDictSize[var1];
         if (var1 <= 3) {
            this.mode = 1;
            this.mf = 4;
            this.niceLen = var1 <= 1 ? 128 : 273;
            this.depthLimit = presetToDepthLimit[var1];
         } else {
            this.mode = 2;
            this.mf = 20;
            this.niceLen = var1 == 4 ? 16 : (var1 == 5 ? 32 : 64);
            this.depthLimit = 0;
         }

      } else {
         throw new UnsupportedOptionsException("Unsupported preset: " + var1);
      }
   }

   public void setDictSize(int var1) throws UnsupportedOptionsException {
      if (var1 < 4096) {
         throw new UnsupportedOptionsException("LZMA2 dictionary size must be at least 4 KiB: " + var1 + " B");
      } else if (var1 > 805306368) {
         throw new UnsupportedOptionsException("LZMA2 dictionary size must not exceed 768 MiB: " + var1 + " B");
      } else {
         this.dictSize = var1;
      }
   }

   public int getDictSize() {
      return this.dictSize;
   }

   public void setPresetDict(byte[] var1) {
      this.presetDict = var1;
   }

   public byte[] getPresetDict() {
      return this.presetDict;
   }

   public void setLcLp(int var1, int var2) throws UnsupportedOptionsException {
      if (var1 >= 0 && var2 >= 0 && var1 <= 4 && var2 <= 4 && var1 + var2 <= 4) {
         this.lc = var1;
         this.lp = var2;
      } else {
         throw new UnsupportedOptionsException("lc + lp must not exceed 4: " + var1 + " + " + var2);
      }
   }

   public void setLc(int var1) throws UnsupportedOptionsException {
      this.setLcLp(var1, this.lp);
   }

   public void setLp(int var1) throws UnsupportedOptionsException {
      this.setLcLp(this.lc, var1);
   }

   public int getLc() {
      return this.lc;
   }

   public int getLp() {
      return this.lp;
   }

   public void setPb(int var1) throws UnsupportedOptionsException {
      if (var1 >= 0 && var1 <= 4) {
         this.pb = var1;
      } else {
         throw new UnsupportedOptionsException("pb must not exceed 4: " + var1);
      }
   }

   public int getPb() {
      return this.pb;
   }

   public void setMode(int var1) throws UnsupportedOptionsException {
      if (var1 >= 0 && var1 <= 2) {
         this.mode = var1;
      } else {
         throw new UnsupportedOptionsException("Unsupported compression mode: " + var1);
      }
   }

   public int getMode() {
      return this.mode;
   }

   public void setNiceLen(int var1) throws UnsupportedOptionsException {
      if (var1 < 8) {
         throw new UnsupportedOptionsException("Minimum nice length of matches is 8 bytes: " + var1);
      } else if (var1 > 273) {
         throw new UnsupportedOptionsException("Maximum nice length of matches is 273: " + var1);
      } else {
         this.niceLen = var1;
      }
   }

   public int getNiceLen() {
      return this.niceLen;
   }

   public void setMatchFinder(int var1) throws UnsupportedOptionsException {
      if (var1 != 4 && var1 != 20) {
         throw new UnsupportedOptionsException("Unsupported match finder: " + var1);
      } else {
         this.mf = var1;
      }
   }

   public int getMatchFinder() {
      return this.mf;
   }

   public void setDepthLimit(int var1) throws UnsupportedOptionsException {
      if (var1 < 0) {
         throw new UnsupportedOptionsException("Depth limit cannot be negative: " + var1);
      } else {
         this.depthLimit = var1;
      }
   }

   public int getDepthLimit() {
      return this.depthLimit;
   }

   public int getEncoderMemoryUsage() {
      return this.mode == 0 ? UncompressedLZMA2OutputStream.getMemoryUsage() : LZMA2OutputStream.getMemoryUsage(this);
   }

   public FinishableOutputStream getOutputStream(FinishableOutputStream var1, ArrayCache var2) {
      return (FinishableOutputStream)(this.mode == 0 ? new UncompressedLZMA2OutputStream(var1, var2) : new LZMA2OutputStream(var1, this, var2));
   }

   public int getDecoderMemoryUsage() {
      int var1 = this.dictSize - 1;
      var1 = var1 | var1 >>> 2;
      var1 = var1 | var1 >>> 3;
      var1 = var1 | var1 >>> 4;
      var1 = var1 | var1 >>> 8;
      var1 = var1 | var1 >>> 16;
      return LZMA2InputStream.getMemoryUsage(var1 + 1);
   }

   public InputStream getInputStream(InputStream var1, ArrayCache var2) throws IOException {
      return new LZMA2InputStream(var1, this.dictSize, this.presetDict, var2);
   }

   FilterEncoder getFilterEncoder() {
      return new LZMA2Encoder(this);
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         assert false;

         throw new RuntimeException();
      }
   }
}
