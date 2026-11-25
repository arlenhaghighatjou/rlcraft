package rtg.api.world.deco;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.gen.feature.WorldGenBlock;

public class DecoCobwebs extends DecoBase {
   private static final IBlockState WEB = Blocks.WEB.getDefaultState();
   private static final IBlockState AIR = Blocks.AIR.getDefaultState();
   private float strengthFactor;
   private int minY;
   private int maxY;
   private int chance;
   private IBlockState adjacentBlock;
   private int minAdjacents;

   public DecoCobwebs() {
      this.setStrengthFactor(2.0F);
      this.setMinY(1);
      this.setMaxY(255);
      this.setChance(10);
      this.setAdjacentBlock(Blocks.AIR.getDefaultState());
      this.setMinAdjacents(1);
      this.addDecoTypes(new DecoBase.DecoType[]{DecoBase.DecoType.COBWEB});
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, EaglercraftRandom rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      if (TerrainGen.decorate(rtgWorld.world(), rand, chunkPos, EventType.CUSTOM)) {
         for(int i = 0; (float)i < this.strengthFactor; ++i) {
            if (rand.nextInt(this.chance) == 0) {
               (new WorldGenBlock(WEB, AIR, this.adjacentBlock, this.minAdjacents)).generate(rtgWorld.world(), rand, getOffsetPos(chunkPos).add(rand.nextInt(16), getRangedRandom(rand, this.minY, this.maxY), rand.nextInt(16)));
            }
         }
      }

   }

   public float getStrengthFactor() {
      return this.strengthFactor;
   }

   public DecoCobwebs setStrengthFactor(float strengthFactor) {
      this.strengthFactor = strengthFactor;
      return this;
   }

   public int getMinY() {
      return this.minY;
   }

   public DecoCobwebs setMinY(int minY) {
      this.minY = minY;
      return this;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public DecoCobwebs setMaxY(int maxY) {
      this.maxY = maxY;
      return this;
   }

   public int getChance() {
      return this.chance;
   }

   public DecoCobwebs setChance(int chance) {
      this.chance = chance;
      return this;
   }

   public IBlockState getAdjacentBlock() {
      return this.adjacentBlock;
   }

   public DecoCobwebs setAdjacentBlock(IBlockState adjacentBlock) {
      this.adjacentBlock = adjacentBlock;
      return this;
   }

   public int getMinAdjacents() {
      return this.minAdjacents;
   }

   public DecoCobwebs setMinAdjacents(int minAdjacents) {
      this.minAdjacents = minAdjacents;
      return this;
   }
}
