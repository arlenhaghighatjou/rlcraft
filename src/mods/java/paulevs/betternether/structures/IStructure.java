package paulevs.betternether.structures;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IStructure {
   void generate(World var1, BlockPos var2, EaglercraftRandom var3);
}
