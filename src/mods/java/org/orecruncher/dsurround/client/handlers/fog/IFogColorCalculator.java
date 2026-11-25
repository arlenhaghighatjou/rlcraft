package org.orecruncher.dsurround.client.handlers.fog;

import javax.annotation.Nonnull;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.Color;

@SideOnly(Side.CLIENT)
public interface IFogColorCalculator {
   @Nonnull
   Color calculate(@Nonnull FogColors var1);

   void tick();
}
