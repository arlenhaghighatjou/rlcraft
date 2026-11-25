package org.orecruncher.dsurround.client.aurora;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IAurora {
   void setFading(boolean var1);

   boolean isDying();

   void update();

   boolean isComplete();

   void render(float var1);
}
