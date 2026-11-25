package org.orecruncher.dsurround.client.footsteps.facade;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
final class CoFHCoreCoverAccessor extends FacadeAccessor {
   private static final String CLASS = "cofh.core.render.IBlockAppearance";
   private static final String METHOD = "getVisualState";

   public CoFHCoreCoverAccessor() {
      super("cofh.core.render.IBlockAppearance", "getVisualState");
   }
}
