package vazkii.quark.experimental.lighting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IColoredLightSource {
   float[][] VANILLA_SPECTRUM_COLORS = new float[][]{{1.0F, 1.0F, 1.0F}, {1.0F, 0.5F, 0.0F}, {1.0F, 0.0F, 1.0F}, {0.0F, 0.5F, 1.0F}, {1.0F, 1.0F, 0.0F}, {0.5F, 1.0F, 0.0F}, {1.0F, 0.0F, 0.5F}, {1.0F, 1.0F, 1.0F}, {1.0F, 1.0F, 1.0F}, {0.0F, 1.0F, 1.0F}, {0.5F, 0.0F, 1.0F}, {0.0F, 0.0F, 1.0F}, {0.5F, 0.25F, 0.0F}, {0.0F, 1.0F, 0.0F}, {1.0F, 0.0F, 0.0F}, {1.0F, 1.0F, 1.0F}};

   float[] getColoredLight(IBlockAccess var1, BlockPos var2);
}
