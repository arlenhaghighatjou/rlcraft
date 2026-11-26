package fionathemortal.betterbiomeblend;

public final class LocalColorChunk {
   private ColorChunk chunk;

   public LocalColorChunk() {
      this.chunk = new ColorChunk();
      this.chunk.acquire();
   }

   public ColorChunk get() {
      return this.chunk;
   }

   public void set(ColorChunk chunk) {
      this.chunk = chunk;
   }
}
