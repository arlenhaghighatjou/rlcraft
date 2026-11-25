package com.creativemd.creativecore.client.avatar;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class Avatar {
   public Avatar() {
   }

   public abstract void handleRendering(GuiRenderHelper var1, int var2, int var3);
}
