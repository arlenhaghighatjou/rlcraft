package com.endertech.minecraft.forge.regs;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface INeedRender {
   @SideOnly(Side.CLIENT)
   void registerRender();
}
