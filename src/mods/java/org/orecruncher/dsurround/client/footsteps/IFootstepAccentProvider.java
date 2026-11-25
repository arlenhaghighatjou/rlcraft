package org.orecruncher.dsurround.client.footsteps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.acoustics.IAcoustic;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public interface IFootstepAccentProvider {
   String getName();

   void provide(@Nonnull EntityLivingBase var1, @Nullable BlockPos var2, @Nonnull ObjectArray<IAcoustic> var3);
}
