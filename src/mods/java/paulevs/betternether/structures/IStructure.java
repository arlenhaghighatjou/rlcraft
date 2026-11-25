package paulevs.betternether.structures;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IStructure {
   void generate(World var1, BlockPos var2, Random var3);
}
