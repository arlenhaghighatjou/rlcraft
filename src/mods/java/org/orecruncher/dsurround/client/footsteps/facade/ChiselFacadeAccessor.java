package org.orecruncher.dsurround.client.footsteps.facade;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
final class ChiselFacadeAccessor extends FacadeAccessor {
   private static final String CLASS = "team.chisel.api.IFacade";
   private static final String METHOD = "getFacade";

   public ChiselFacadeAccessor() {
      super("team.chisel.api.IFacade", "getFacade");
   }
}
