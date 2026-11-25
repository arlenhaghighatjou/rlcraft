package hunternif.mc.atlas.core;

import java.util.Random;

public class Tile {
   public final int biomeID;
   private static final Random RANDOM = new Random();
   private transient short variationNumber;

   public Tile(int biomeID) {
      this(biomeID, (byte)0);
      this.randomizeTexture();
   }

   private Tile(int biomeID, byte variationNumber) {
      this.biomeID = biomeID;
      this.variationNumber = (short)variationNumber;
   }

   private void randomizeTexture() {
      this.variationNumber = (short)RANDOM.nextInt(32767);
   }

   public short getVariationNumber() {
      return this.variationNumber;
   }

   public String toString() {
      return "tile" + this.biomeID;
   }

   public boolean equals(Object obj) {
      return obj instanceof Tile && ((Tile)obj).biomeID == this.biomeID;
   }
}
