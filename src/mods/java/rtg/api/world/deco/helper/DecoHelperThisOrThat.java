package rtg.api.world.deco.helper;

import java.util.Random;
import net.minecraft.util.math.ChunkPos;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.deco.DecoBase;

public class DecoHelperThisOrThat extends DecoBase {
   private int chance;
   private DecoHelperThisOrThat.ChanceType chanceType;
   private DecoBase decoThis;
   private DecoBase decoThat;

   public DecoHelperThisOrThat(int chance, DecoHelperThisOrThat.ChanceType chanceType, DecoBase decoThis, DecoBase decoThat) {
      this.chance = chance;
      this.chanceType = chanceType;
      this.decoThis = decoThis;
      this.decoThat = decoThat;
      if (!decoThis.properlyDefined()) {
         throw new RuntimeException();
      } else if (!decoThat.properlyDefined()) {
         throw new RuntimeException();
      }
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, Random rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      switch(this.chanceType) {
      case EQUALS_ZERO:
         if (rand.nextInt(this.chance) == 0) {
            this.decoThis.generate(biome, rtgWorld, rand, chunkPos, river, hasVillage);
         } else {
            this.decoThat.generate(biome, rtgWorld, rand, chunkPos, river, hasVillage);
         }
         break;
      case NOT_EQUALS_ZERO:
         if (rand.nextInt(this.chance) != 0) {
            this.decoThis.generate(biome, rtgWorld, rand, chunkPos, river, hasVillage);
         } else {
            this.decoThat.generate(biome, rtgWorld, rand, chunkPos, river, hasVillage);
         }
      }

   }

   public int getChance() {
      return this.chance;
   }

   public DecoHelperThisOrThat setChance(int chance) {
      this.chance = chance;
      return this;
   }

   public DecoHelperThisOrThat.ChanceType getChanceType() {
      return this.chanceType;
   }

   public DecoHelperThisOrThat setChanceType(DecoHelperThisOrThat.ChanceType chanceType) {
      this.chanceType = chanceType;
      return this;
   }

   public static enum ChanceType {
      EQUALS_ZERO,
      NOT_EQUALS_ZERO;

      private ChanceType() {
      }
   }
}
