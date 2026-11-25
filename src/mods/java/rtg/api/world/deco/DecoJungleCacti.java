package rtg.api.world.deco;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.gen.feature.WorldGenJungleCacti;

public class DecoJungleCacti extends DecoBase {
   private float strengthFactor;
   private int maxY;
   private boolean sandOnly;
   private int extraHeight;

   public DecoJungleCacti() {
      this.setStrengthFactor(8.0F);
      this.setMaxY(255);
      this.setSandOnly(false);
      this.setExtraHeight(7);
      this.addDecoTypes(new DecoBase.DecoType[]{DecoBase.DecoType.CACTUS});
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, Random rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      if (TerrainGen.decorate(rtgWorld.world(), rand, chunkPos, EventType.CACTUS)) {
         for(int i = 0; (float)i < this.strengthFactor; ++i) {
            BlockPos pos = getOffsetPos(chunkPos).add(rand.nextInt(16), rand.nextInt(this.maxY), rand.nextInt(16));
            (new WorldGenJungleCacti(this.sandOnly, rand.nextInt(this.extraHeight))).generate(rtgWorld.world(), rand, pos);
         }
      }

   }

   public float getStrengthFactor() {
      return this.strengthFactor;
   }

   public DecoJungleCacti setStrengthFactor(float strengthFactor) {
      this.strengthFactor = strengthFactor;
      return this;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public DecoJungleCacti setMaxY(int maxY) {
      this.maxY = maxY;
      return this;
   }

   public boolean isSandOnly() {
      return this.sandOnly;
   }

   public DecoJungleCacti setSandOnly(boolean sandOnly) {
      this.sandOnly = sandOnly;
      return this;
   }

   public int getExtraHeight() {
      return this.extraHeight;
   }

   public DecoJungleCacti setExtraHeight(int extraHeight) {
      this.extraHeight = extraHeight;
      return this;
   }
}
