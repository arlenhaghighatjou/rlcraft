package rtg.api.world.deco;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.gen.feature.WorldGenCacti;

public class DecoCactus extends DecoBase {
   private int loops;
   private int chance;
   private float strengthFactor;
   private int maxY;
   private boolean sandOnly;
   private IBlockState soilBlock;

   public DecoCactus() {
      this.setLoops(1);
      this.setChance(1);
      this.setMaxY(255);
      this.setStrengthFactor(0.0F);
      this.setSandOnly(false);
      this.setSoilBlock(Blocks.SAND.getDefaultState());
      this.addDecoTypes(new DecoBase.DecoType[]{DecoBase.DecoType.CACTUS});
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, Random rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      if (TerrainGen.decorate(rtgWorld.world(), rand, chunkPos, EventType.CACTUS)) {
         int loopCount = this.strengthFactor > 0.0F ? (int)this.strengthFactor : this.loops;

         for(int i = 0; i < loopCount; ++i) {
            BlockPos pos = getOffsetPos(chunkPos).add(rand.nextInt(16), 0, rand.nextInt(16));
            int y = rtgWorld.world().getHeight(pos).getY();
            if (y <= this.maxY && rand.nextInt(this.chance) == 0) {
               (new WorldGenCacti(this.sandOnly, 0, this.soilBlock)).generate(rtgWorld.world(), rand, pos.up(y));
            }
         }
      }

   }

   public int getLoops() {
      return this.loops;
   }

   public DecoCactus setLoops(int loops) {
      this.loops = loops;
      return this;
   }

   public int getChance() {
      return this.chance;
   }

   public DecoCactus setChance(int chance) {
      this.chance = chance;
      return this;
   }

   public float getStrengthFactor() {
      return this.strengthFactor;
   }

   public DecoCactus setStrengthFactor(float strengthFactor) {
      this.strengthFactor = strengthFactor;
      return this;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public DecoCactus setMaxY(int maxY) {
      this.maxY = maxY;
      return this;
   }

   public boolean isSandOnly() {
      return this.sandOnly;
   }

   public DecoCactus setSandOnly(boolean sandOnly) {
      this.sandOnly = sandOnly;
      return this;
   }

   public IBlockState getSoilBlock() {
      return this.soilBlock;
   }

   public DecoCactus setSoilBlock(IBlockState soilBlock) {
      this.soilBlock = soilBlock;
      return this;
   }
}
