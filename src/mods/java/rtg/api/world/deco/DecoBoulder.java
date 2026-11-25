package rtg.api.world.deco;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import rtg.api.util.BlockUtil;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.gen.feature.WorldGenBlob;

public class DecoBoulder extends DecoBase {
   private IBlockState boulderBlock;
   private float strengthFactor;
   private int minY;
   private int maxY;
   private DecoBoulder.HeightType heightType;
   private int chance;
   private boolean water;
   private List<Block> validGroundBlocks;

   public DecoBoulder() {
      this.setBoulderBlock(Blocks.COBBLESTONE.getDefaultState());
      this.setStrengthFactor(2.0F);
      this.setMinY(60);
      this.setMaxY(255);
      this.setHeightType(DecoBoulder.HeightType.GET_HEIGHT_VALUE);
      this.setChance(10);
      this.water = true;
      this.validGroundBlocks = Arrays.asList(Blocks.GRASS, Blocks.DIRT, Blocks.STONE, Blocks.GRAVEL, Blocks.CLAY, Blocks.SAND);
      this.addDecoTypes(new DecoBase.DecoType[]{DecoBase.DecoType.BOULDER});
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, EaglercraftRandom rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      if (rtgWorld.getGeneratorSettings().useBoulders && TerrainGen.decorate(rtgWorld.world(), rand, chunkPos, EventType.ROCK)) {
         for(int i = 0; (float)i < this.strengthFactor; ++i) {
            BlockPos pos = getOffsetPos(chunkPos).add(rand.nextInt(16), 0, rand.nextInt(16));
            int y;
            switch(this.heightType) {
            case NEXT_INT:
               y = getRangedRandom(rand, this.minY, this.maxY);
               break;
            case GET_HEIGHT_VALUE:
            default:
               y = rtgWorld.world().getHeight(pos).getY();
            }

            if (y >= this.minY && y <= this.maxY && rand.nextInt(this.chance) == 0 && (!hasVillage || BlockUtil.checkAreaMaterials(BlockUtil.MatchType.ALL_IGNORE_REPLACEABLE, rtgWorld.world(), pos.up(y), 2))) {
               (new WorldGenBlob(this.boulderBlock, 0, this.validGroundBlocks, this.water)).generate(rtgWorld.world(), rand, pos.up(y));
            }
         }
      }

   }

   public IBlockState getBoulderBlock() {
      return this.boulderBlock;
   }

   public DecoBoulder setBoulderBlock(IBlockState boulderBlock) {
      this.boulderBlock = boulderBlock;
      return this;
   }

   public float getStrengthFactor() {
      return this.strengthFactor;
   }

   public DecoBoulder setStrengthFactor(float strengthFactor) {
      this.strengthFactor = strengthFactor;
      return this;
   }

   public int getMinY() {
      return this.minY;
   }

   public DecoBoulder setMinY(int minY) {
      this.minY = minY;
      return this;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public DecoBoulder setMaxY(int maxY) {
      this.maxY = maxY;
      return this;
   }

   public int getChance() {
      return this.chance;
   }

   public DecoBoulder setChance(int chance) {
      this.chance = chance;
      return this;
   }

   public boolean isWater() {
      return this.water;
   }

   public DecoBoulder setWater(boolean water) {
      this.water = water;
      return this;
   }

   public DecoBoulder.HeightType getHeightType() {
      return this.heightType;
   }

   public DecoBoulder setHeightType(DecoBoulder.HeightType heightType) {
      this.heightType = heightType;
      return this;
   }

   public List<Block> getValidGroundBlocks() {
      return Collections.unmodifiableList(this.validGroundBlocks);
   }

   public DecoBoulder setValidGroundBlocks(ArrayList<Block> validGroundBlocks) {
      this.validGroundBlocks = Collections.unmodifiableList(validGroundBlocks);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public static enum HeightType {
      NEXT_INT,
      GET_HEIGHT_VALUE;

      private HeightType() {
      }
   }
}
