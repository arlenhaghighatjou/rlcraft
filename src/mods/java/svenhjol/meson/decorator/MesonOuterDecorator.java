package svenhjol.meson.decorator;

import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public abstract class MesonOuterDecorator {
   protected World world;
   protected BlockPos pos;
   protected EaglercraftRandom rand;
   protected List<ChunkPos> chunks;

   public MesonOuterDecorator(World world, BlockPos pos, EaglercraftRandom rand, List<ChunkPos> chunks) {
      this.world = world;
      this.pos = pos;
      this.rand = rand;
      this.chunks = chunks;
   }

   public abstract void generate();
}
