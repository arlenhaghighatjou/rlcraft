package rtg.api.world.deco;

import java.util.ArrayList;
import java.util.Arrays;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSponge;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.gen.feature.WorldGenSponge;

public class DecoSponge extends DecoBase {
   protected ArrayList<Block> validGroundBlocks;
   private IBlockState spongeBlock;
   private float strengthFactor;
   private int minY;
   private int maxY;
   private DecoSponge.HeightType heightType;
   private int chance;
   private boolean water;

   public DecoSponge() {
      this.setSpongeBlock(Blocks.SPONGE.getDefaultState().withProperty(BlockSponge.WET, Boolean.valueOf(true)));
      this.setStrengthFactor(2.0F);
      this.setMinY(20);
      this.setMaxY(45);
      this.setHeightType(DecoSponge.HeightType.NEXT_INT);
      this.setChance(10);
      this.water = true;
      this.validGroundBlocks = new ArrayList(Arrays.asList(Blocks.GRASS, Blocks.DIRT, Blocks.STONE, Blocks.GRAVEL, Blocks.CLAY, Blocks.SAND));
      this.addDecoTypes(new DecoBase.DecoType[]{DecoBase.DecoType.SPONGE});
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, EaglercraftRandom rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      for(int i = 0; (float)i < this.strengthFactor; ++i) {
         BlockPos pos = getOffsetPos(chunkPos).add(rand.nextInt(16), 0, rand.nextInt(16));
         int y;
         switch(this.heightType) {
         case NEXT_INT:
            y = getRangedRandom(rand, this.minY, this.maxY);
            break;
         case GET_HEIGHT_VALUE:
            y = rtgWorld.world().getHeight(pos).getY();
            break;
         default:
            y = rtgWorld.world().getHeight(pos).getY();
         }

         if (y >= this.minY && y <= this.maxY && rand.nextInt(this.chance) == 0) {
            (new WorldGenSponge(this.spongeBlock, 0, rand, this.validGroundBlocks)).generate(rtgWorld.world(), rand, pos.up(y));
         }
      }

   }

   public IBlockState getSpongeBlock() {
      return this.spongeBlock;
   }

   public DecoSponge setSpongeBlock(IBlockState spongeBlock) {
      this.spongeBlock = spongeBlock;
      return this;
   }

   public float getStrengthFactor() {
      return this.strengthFactor;
   }

   public DecoSponge setStrengthFactor(float strengthFactor) {
      this.strengthFactor = strengthFactor;
      return this;
   }

   public int getMinY() {
      return this.minY;
   }

   public DecoSponge setMinY(int minY) {
      this.minY = minY;
      return this;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public DecoSponge setMaxY(int maxY) {
      this.maxY = maxY;
      return this;
   }

   public int getChance() {
      return this.chance;
   }

   public DecoSponge setChance(int chance) {
      this.chance = chance;
      return this;
   }

   public boolean isWater() {
      return this.water;
   }

   public DecoSponge setWater(boolean water) {
      this.water = water;
      return this;
   }

   public DecoSponge.HeightType getHeightType() {
      return this.heightType;
   }

   public DecoSponge setHeightType(DecoSponge.HeightType heightType) {
      this.heightType = heightType;
      return this;
   }

   public ArrayList<Block> getValidGroundBlocks() {
      return this.validGroundBlocks;
   }

   public DecoSponge setValidGroundBlocks(ArrayList<Block> validGroundBlocks) {
      this.validGroundBlocks = validGroundBlocks;
      return this;
   }

   public static enum HeightType {
      NEXT_INT,
      GET_HEIGHT_VALUE;

      private HeightType() {
      }
   }
}
