package rtg.api.world.deco;

import java.util.ArrayList;
import java.util.Arrays;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;

public abstract class DecoBase {
   /** @deprecated */
   @Deprecated
   private ArrayList<DecoBase.DecoType> decoTypes = new ArrayList();
   private boolean checkRiver = false;
   private float minRiver = -2.0F;
   private float maxRiver;

   public DecoBase() {
      this.setMaxRiver(2.0F);
   }

   public static void tweakTreeLeaves(DecoTree deco, boolean checkDecay, boolean decayable) {
      if (deco.getLeavesBlock().getBlock() instanceof BlockLeaves) {
         IBlockState leaves = deco.getLeavesBlock().withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(checkDecay)).withProperty(BlockLeaves.DECAYABLE, Boolean.valueOf(decayable));
         deco.setLeavesBlock(leaves);
      }

   }

   public static void tweakShrubLeaves(DecoShrub deco, boolean checkDecay, boolean decayable) {
      if (deco.getLeavesBlock().getBlock() instanceof BlockLeaves) {
         IBlockState leaves = deco.getLeavesBlock().withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(checkDecay)).withProperty(BlockLeaves.DECAYABLE, Boolean.valueOf(decayable));
         deco.setLeavesBlock(leaves);
      }

   }

   /** @deprecated */
   @Deprecated
   public boolean properlyDefined() {
      return true;
   }

   public boolean preGenerate(float river) {
      return !this.checkRiver || river <= this.maxRiver && river >= this.minRiver;
   }

   public abstract void generate(IRealisticBiome var1, RTGWorld var2, EaglercraftRandom var3, ChunkPos var4, float var5, boolean var6);

   /** @deprecated */
   @Deprecated
   public void addDecoTypes(DecoBase.DecoType... decos) {
      this.decoTypes.addAll(Arrays.asList(decos));
   }

   /** @deprecated */
   @Deprecated
   public ArrayList<DecoBase.DecoType> getDecoTypes() {
      return this.decoTypes;
   }

   /** @deprecated */
   @Deprecated
   public DecoBase setDecoTypes(ArrayList<DecoBase.DecoType> decoTypes) {
      this.decoTypes = decoTypes;
      return this;
   }

   public boolean isCheckRiver() {
      return this.checkRiver;
   }

   public DecoBase setCheckRiver(boolean checkRiver) {
      this.checkRiver = checkRiver;
      return this;
   }

   public float getMinRiver() {
      return this.minRiver;
   }

   public DecoBase setMinRiver(float minRiver) {
      this.minRiver = minRiver;
      return this;
   }

   public float getMaxRiver() {
      return this.maxRiver;
   }

   public DecoBase setMaxRiver(float maxRiver) {
      this.maxRiver = maxRiver;
      return this;
   }

   static BlockPos getOffsetPos(ChunkPos pos) {
      return new BlockPos(pos.x * 16 + 8, 0, pos.z * 16 + 8);
   }

   public static int getRangedRandom(EaglercraftRandom rand, int min, int max) {
      return min + rand.nextInt(max - min + 1);
   }

   /** @deprecated */
   @Deprecated
   public static enum DecoType {
      BASE_BIOME_DECORATION,
      BIG_SHROOM,
      BOULDER,
      COBWEB,
      CACTUS,
      DEAD_BUSH,
      DESERT_WELL,
      FALLEN_LEAVES,
      FALLEN_TREE,
      FERN,
      FERN_DOUBLE,
      FLOWER,
      GRASS,
      GRASS_DOUBLE,
      LAYER,
      LEAVES,
      LILYPAD,
      MUSHROOM,
      PUMPKIN,
      REED,
      ROCK_SPIRE,
      SEAWEED,
      SHRUB,
      SPONGE,
      TEST,
      TREE,
      VINE,
      WHEAT,
      WORLDGEN;

      private DecoType() {
      }
   }
}
