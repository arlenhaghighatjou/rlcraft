package rtg.api.world.deco;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;

public class DecoPumpkin extends DecoBase {
   private float strengthFactor;
   private int maxY;
   private float randomFloat;
   /** @deprecated */
   @Deprecated
   private DecoPumpkin.RandomType randomType;
   private int chance;
   private int loops;

   public DecoPumpkin() {
      this.setMaxY(255);
      this.setStrengthFactor(0.0F);
      this.setRandomType(DecoPumpkin.RandomType.USE_CHANCE_VALUE);
      this.setRandomFloat(1.0F);
      this.setChance(1);
      this.setLoops(1);
      this.addDecoTypes(new DecoBase.DecoType[]{DecoBase.DecoType.PUMPKIN});
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, Random rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      if (TerrainGen.decorate(rtgWorld.world(), rand, chunkPos, EventType.PUMPKIN)) {
         int loopCount;
         int i;
         switch(this.randomType) {
         case ALWAYS_GENERATE:
            this.setChance(1);
         case USE_CHANCE_VALUE:
         default:
            loopCount = this.strengthFactor > 0.0F ? (int)this.strengthFactor : this.loops;
            i = 0;
         }

         for(; i < loopCount; ++i) {
            if (rand.nextInt(this.chance) == 0) {
               BlockPos pos = getOffsetPos(chunkPos).add(rand.nextInt(16), rand.nextInt(this.maxY), rand.nextInt(16));
               (new WorldGenPumpkin()).generate(rtgWorld.world(), rand, pos);
            }
         }
      }

   }

   public float getStrengthFactor() {
      return this.strengthFactor;
   }

   public DecoPumpkin setStrengthFactor(float strengthFactor) {
      this.strengthFactor = strengthFactor;
      return this;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public DecoPumpkin setMaxY(int maxY) {
      this.maxY = maxY;
      return this;
   }

   public float getRandomFloat() {
      return this.randomFloat;
   }

   public DecoPumpkin setRandomFloat(float randomFloat) {
      this.randomFloat = randomFloat;
      return this;
   }

   public DecoPumpkin.RandomType getRandomType() {
      return this.randomType;
   }

   public DecoPumpkin setRandomType(DecoPumpkin.RandomType randomType) {
      this.randomType = randomType;
      return this;
   }

   public int getChance() {
      return this.chance;
   }

   public DecoPumpkin setChance(int chance) {
      this.chance = chance;
      return this;
   }

   public int getLoops() {
      return this.loops;
   }

   public DecoPumpkin setLoops(int loops) {
      this.loops = loops;
      return this;
   }

   /** @deprecated */
   @Deprecated
   public static enum RandomType {
      ALWAYS_GENERATE,
      USE_CHANCE_VALUE;

      private RandomType() {
      }
   }
}
