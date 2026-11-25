package rtg.api.world.deco;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;

public class DecoReed extends DecoBase {
   private float strengthFactor;
   private int maxY;
   private int loops;

   public DecoReed() {
      this.setMaxY(255);
      this.setStrengthFactor(0.0F);
      this.setLoops(1);
      this.addDecoTypes(new DecoBase.DecoType[]{DecoBase.DecoType.REED});
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, EaglercraftRandom rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      if (TerrainGen.decorate(rtgWorld.world(), rand, chunkPos, EventType.REED)) {
         int loopCount = this.strengthFactor > 0.0F ? (int)this.strengthFactor : this.loops;

         for(int i = 0; i < loopCount; ++i) {
            BlockPos pos = getOffsetPos(chunkPos).add(rand.nextInt(16), rand.nextInt(this.maxY), rand.nextInt(16));
            (new WorldGenReed()).generate(rtgWorld.world(), rand, pos);
         }
      }

   }

   public float getStrengthFactor() {
      return this.strengthFactor;
   }

   public DecoReed setStrengthFactor(float strengthFactor) {
      this.strengthFactor = strengthFactor;
      return this;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public DecoReed setMaxY(int maxY) {
      this.maxY = maxY;
      return this;
   }

   public int getLoops() {
      return this.loops;
   }

   public DecoReed setLoops(int loops) {
      this.loops = loops;
      return this;
   }
}
