package com.teamacronymcoders.base.items.tools;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public interface IAreaBreakingTool {
   RayTraceResult rayTrace(@Nonnull World var1, @Nonnull EntityPlayer var2, boolean var3);
}
