package org.orecruncher.dsurround.client.footsteps.facade;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
interface IFacadeAccessor {
   @Nonnull
   String getName();

   boolean instanceOf(@Nonnull Block var1);

   boolean isValid();

   @Nullable
   IBlockState getBlockState(@Nonnull EntityLivingBase var1, @Nonnull IBlockState var2, @Nonnull IBlockAccess var3, @Nonnull Vec3d var4, @Nullable EnumFacing var5);
}
