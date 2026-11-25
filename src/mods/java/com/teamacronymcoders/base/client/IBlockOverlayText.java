package com.teamacronymcoders.base.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBlockOverlayText {
   @SideOnly(Side.CLIENT)
   String[] getOverlayText(EntityPlayer var1, RayTraceResult var2, boolean var3);
}
