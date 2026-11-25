package me.ichun.mods.ichunutil.api.worldportals;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IApi {
   @SideOnly(Side.CLIENT)
   int getRenderLevel();

   @SideOnly(Side.CLIENT)
   float getCameraRoll(int var1, float var2);
}
