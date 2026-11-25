package vazkii.quark.world.world;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.world.feature.Basalt;
import vazkii.quark.world.feature.RevampStoneGen;

public class BasaltGenerator extends StoneInfoBasedGenerator implements IWorldGenerator {
   public BasaltGenerator(Supplier<RevampStoneGen.StoneInfo> infoSupplier) {
      super(infoSupplier, Basalt.basalt.getDefaultState(), "basalt");
   }

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      this.generate(chunkX, chunkZ, world);
   }

   public boolean canPlaceBlock(World world, BlockPos pos) {
      Block block = world.getBlockState(pos).getBlock();
      return block == Blocks.STONE || block == Blocks.NETHERRACK;
   }
}
