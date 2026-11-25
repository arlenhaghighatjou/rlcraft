package rtg.api.world.deco.helper;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.ChunkPos;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.deco.DecoBase;

public class DecoHelper5050 extends DecoBase {
   private DecoBase deco1;
   private DecoBase deco2;

   public DecoHelper5050(DecoBase deco1, DecoBase deco2) {
      if (!(deco1 instanceof DecoHelper5050) && !(deco2 instanceof DecoHelper5050)) {
         this.deco1 = deco1;
         this.deco2 = deco2;
      } else {
         throw new RuntimeException("DecoHelper5050 cannot accept itself as a parameter.");
      }
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, EaglercraftRandom rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      if (rand.nextBoolean()) {
         this.deco1.generate(biome, rtgWorld, rand, chunkPos, river, hasVillage);
      } else {
         this.deco2.generate(biome, rtgWorld, rand, chunkPos, river, hasVillage);
      }

   }

   public DecoBase getDeco1() {
      return this.deco1;
   }

   public DecoHelper5050 setDeco1(DecoBase deco1) {
      this.deco1 = deco1;
      return this;
   }

   public DecoBase getDeco2() {
      return this.deco2;
   }

   public DecoHelper5050 setDeco2(DecoBase deco2) {
      this.deco2 = deco2;
      return this;
   }
}
