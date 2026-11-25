package rtg.api.world.deco;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.gen.feature.WorldGenDoublePlantRTG;

public class DecoDoubleGrass extends DecoBase {
   private float strengthFactor;
   private int maxY;
   private int loops;

   public DecoDoubleGrass() {
      this.setMaxY(255);
      this.setStrengthFactor(0.0F);
      this.setLoops(1);
      this.addDecoTypes(new DecoBase.DecoType[]{DecoBase.DecoType.GRASS_DOUBLE});
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, EaglercraftRandom rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      if (TerrainGen.decorate(rtgWorld.world(), rand, chunkPos, EventType.GRASS)) {
         int loopCount = this.strengthFactor > 0.0F ? (int)this.strengthFactor : this.loops;

         for(int i = 0; i < loopCount; ++i) {
            BlockPos pos = getOffsetPos(chunkPos).add(rand.nextInt(16), rand.nextInt(this.maxY), rand.nextInt(16));
            (new WorldGenDoublePlantRTG(EnumPlantType.GRASS)).generate(rtgWorld.world(), rand, pos);
         }
      }

   }

   public float getStrengthFactor() {
      return this.strengthFactor;
   }

   public DecoDoubleGrass setStrengthFactor(float strengthFactor) {
      this.strengthFactor = strengthFactor;
      return this;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public DecoDoubleGrass setMaxY(int maxY) {
      this.maxY = maxY;
      return this;
   }

   public int getLoops() {
      return this.loops;
   }

   public DecoDoubleGrass setLoops(int loops) {
      this.loops = loops;
      return this;
   }
}
