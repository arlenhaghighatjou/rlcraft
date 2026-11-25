package rtg.api.world.biome;

import java.util.Collection;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.RTGConfig;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.world.RTGWorld;
import rtg.api.world.deco.DecoBase;
import rtg.api.world.deco.collection.DecoCollectionBase;
import rtg.api.world.deco.collection.DecoCollectionDesertRiver;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public interface IRealisticBiome {
   Biome baseBiome();

   ResourceLocation baseBiomeResLoc();

   int baseBiomeId();

   RealisticBiomeBase.RiverType getRiverType();

   RealisticBiomeBase.BeachType getBeachType();

   IRealisticBiome getRiverBiome();

   IRealisticBiome getBeachBiome();

   Biome preferredBeach();

   BiomeConfig getConfig();

   TerrainBase terrain();

   SurfaceBase surface();

   void rReplace(ChunkPrimer var1, BlockPos var2, int var3, int var4, int var5, RTGWorld var6, float[] var7, float var8, Biome[] var9);

   void rReplace(ChunkPrimer var1, int var2, int var3, int var4, int var5, int var6, RTGWorld var7, float[] var8, float var9, Biome[] var10);

   float rNoise(RTGWorld var1, int var2, int var3, float var4, float var5);

   double waterLakeMult();

   double lavaLakeMult();

   float lakePressure(RTGWorld var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8);

   void initDecos();

   Collection<DecoBase> getDecos();

   /** @deprecated */
   @Deprecated
   Collection<TreeRTG> getTrees();

   default void addDeco(DecoBase deco, boolean allowed) {
      if (allowed) {
         Collection<DecoBase> decos = this.getDecos();
         if (!deco.properlyDefined()) {
            throw new RuntimeException(deco.toString());
         }

         decos.add(deco);
      }

   }

   default void overrideDecorations() {
   }

   default void addDeco(DecoBase deco) {
      if (!deco.properlyDefined()) {
         throw new RuntimeException(deco.toString());
      } else {
         this.addDeco(deco, true);
      }
   }

   /** @deprecated */
   @Deprecated
   default void addDecoCollection(DecoCollectionBase decoCollection) {
      if (!(decoCollection instanceof DecoCollectionDesertRiver) || RTGConfig.lushRiverbanksInDesert()) {
         if (decoCollection.decos.size() > 0) {
            for(int i = 0; i < decoCollection.decos.size(); ++i) {
               this.addDeco((DecoBase)decoCollection.decos.get(i));
            }
         }

         if (decoCollection.rtgTrees.size() > 0) {
            for(int i = 0; i < decoCollection.rtgTrees.size(); ++i) {
               this.addTree((TreeRTG)decoCollection.rtgTrees.get(i));
            }
         }

      }
   }

   /** @deprecated */
   @Deprecated
   default void addTree(TreeRTG tree, boolean allowed) {
      if (allowed) {
         tree.setSaplingBlock(BlockUtil.getSaplingFromLeaves(tree.getLeavesBlock(), Blocks.SAPLING.getDefaultState()));

         try {
            IBlockState leaves = tree.getLeavesBlock().withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
            tree.setLeavesBlock(leaves);
         } catch (Exception var4) {
            ;
         }

         this.getTrees().add(tree);
      }

   }

   /** @deprecated */
   @Deprecated
   default void addTree(TreeRTG tree) {
      this.addTree(tree, true);
   }

   default void rDecorate(RTGWorld rtgWorld, Random rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      this.getDecos().stream().filter((deco) -> {
         return deco.preGenerate(river);
      }).forEach((deco) -> {
         deco.generate(this, rtgWorld, rand, chunkPos, river, hasVillage);
      });
      if (this.overridesHardcoded()) {
         this.baseBiome().decorator.decorate(rtgWorld.world(), rand, this.baseBiome(), new BlockPos(chunkPos.x * 16, 0, chunkPos.z * 16));
      } else {
         this.baseBiome().decorate(rtgWorld.world(), rand, new BlockPos(chunkPos.x * 16, 0, chunkPos.z * 16));
      }

   }

   default boolean overridesHardcoded() {
      return false;
   }

   TerrainBase initTerrain();

   SurfaceBase initSurface();

   void initConfig();
}
