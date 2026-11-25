package org.orecruncher.dsurround.client.handlers.fog;

import javax.annotation.Nonnull;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IFogRangeCalculator {
   @Nonnull
   String getName();

   @Nonnull
   FogResult calculate(@Nonnull RenderFogEvent var1);

   void tick();
}
