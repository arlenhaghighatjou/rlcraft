package org.orecruncher.dsurround.client.fx;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.chunk.IBlockAccessEx;

@SideOnly(Side.CLIENT)
public interface ISpecialEffect {
   boolean canTrigger(@Nonnull IBlockAccessEx var1, @Nonnull IBlockState var2, @Nonnull BlockPos var3, @Nonnull Random var4);

   void doEffect(@Nonnull IBlockAccessEx var1, @Nonnull IBlockState var2, @Nonnull BlockPos var3, @Nonnull Random var4);
}
