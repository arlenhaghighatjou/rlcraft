package fionathemortal.betterbiomeblend;

import java.util.concurrent.atomic.AtomicInteger;

public final class ColorChunk {
   public byte[] data = new byte[768];
   public long key;
   public int invalidationCounter;
   public AtomicInteger refCount = new AtomicInteger();

   public ColorChunk() {
      this.markAsInvalid();
   }

   public int getReferenceCount() {
      int result = this.refCount.get();
      return result;
   }

   public int release() {
      int result = this.refCount.decrementAndGet();
      return result;
   }

   public void acquire() {
      this.refCount.incrementAndGet();
   }

   public void markAsInvalid() {
      this.key = -2251799780130816L;
   }

   public int getColor(int x, int z) {
      int blockX = x & 15;
      int blockZ = z & 15;
      int offset = 3 * (blockZ << 4 | blockX);
      int colorR = this.data[offset + 0];
      int colorG = this.data[offset + 1];
      int colorB = this.data[offset + 2];
      int result = Color.makeRGBAWithFullAlpha(colorR, colorG, colorB);
      return result;
   }
}
