package com.alcatrazescapee.alcatrazcore.client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModelProvider {
   @SideOnly(Side.CLIENT)
   void registerModel();
}
