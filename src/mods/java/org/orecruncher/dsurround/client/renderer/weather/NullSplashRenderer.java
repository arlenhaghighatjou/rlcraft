package org.orecruncher.dsurround.client.renderer.weather;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NullSplashRenderer extends StormSplashRenderer {
   public NullSplashRenderer() {
   }

   public void addRainParticles(EntityRenderer theThis) {
   }
}
