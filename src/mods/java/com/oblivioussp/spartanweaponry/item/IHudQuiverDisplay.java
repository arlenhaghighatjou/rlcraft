package com.oblivioussp.spartanweaponry.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IHudQuiverDisplay {
   @SideOnly(Side.CLIENT)
   Class<? extends ItemQuiverBase> getRequiredQuiverClass();
}
