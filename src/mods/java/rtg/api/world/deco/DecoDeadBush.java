package rtg.api.world.deco;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;

public class DecoDeadBush extends DecoBase {
   private float strengthFactor;
   private int maxY;
   private int chance;
   private int loops;

   public DecoDeadBush() {
      this.setMaxY(255);
      this.setStrengthFactor(0.0F);
      this.setChance(1);
      this.setLoops(1);
      this.addDecoTypes(new DecoBase.DecoType[]{DecoBase.DecoType.DEAD_BUSH});
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, Random rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      if (TerrainGen.decorate(rtgWorld.world(), rand, chunkPos, EventType.DEAD_BUSH)) {
         int loopCount = this.strengthFactor > 0.0F ? (int)this.strengthFactor : this.loops;

         for(int i = 0; i < loopCount; ++i) {
            BlockPos pos = getOffsetPos(chunkPos).add(rand.nextInt(16), 0, rand.nextInt(16));
            int y = rand.nextInt(this.maxY);
            if (y <= this.maxY && rand.nextInt(this.chance) == 0) {
               (new WorldGenDeadBush()).generate(rtgWorld.world(), rand, pos.up(y));
            }
         }
      }

   }

   public float getStrengthFactor() {
      return this.strengthFactor;
   }

   public DecoDeadBush setStrengthFactor(float strengthFactor) {
      this.strengthFactor = strengthFactor;
      return this;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public DecoDeadBush setMaxY(int maxY) {
      this.maxY = maxY;
      return this;
   }

   public int getChance() {
      return this.chance;
   }

   public DecoDeadBush setChance(int chance) {
      this.chance = chance;
      return this;
   }

   public int getLoops() {
      return this.loops;
   }

   public DecoDeadBush setLoops(int loops) {
      this.loops = loops;
      return this;
   }
}
