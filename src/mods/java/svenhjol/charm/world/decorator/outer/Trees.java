package svenhjol.charm.world.decorator.outer;

import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenBirchTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import svenhjol.charm.world.feature.VillageDecorations;
import svenhjol.meson.decorator.MesonOuterDecorator;

public class Trees extends MesonOuterDecorator {
   public Trees(World world, BlockPos pos, EaglercraftRandom rand, List<ChunkPos> chunks) {
      super(world, pos, rand, chunks);
   }

   public void generate() {
      int max = 3 + this.rand.nextInt(4);
      List<WorldGenerator> generators = new ArrayList();
      generators.add(new WorldGenBigTree(false));
      generators.add(new WorldGenBirchTree(false, false));
      generators.add(new WorldGenTrees(false, 4 + this.rand.nextInt(7), Blocks.LOG.getDefaultState(), Blocks.LEAVES.getDefaultState(), VillageDecorations.treesHaveVines));
      generators.add(new WorldGenCanopyTree(false));

      for(int i = 0; i < max; ++i) {
         int xx = this.rand.nextInt(16) + 8;
         int zz = this.rand.nextInt(16) + 8;
         WorldGenerator gen = (WorldGenerator)generators.get(this.rand.nextInt(generators.size()));
         if (gen != null) {
            BlockPos atPos = this.world.getHeight(this.pos.add(xx, 0, zz));
            gen.generate(this.world, this.rand, atPos);
         }
      }

   }
}
