package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.renderer.RenderCreature;
import javax.vecmath.Vector2f;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerCreatureScrolling extends LayerCreatureEffect {
   public LayerCreatureScrolling(RenderCreature renderer, String textureSuffix, boolean glow, int blending, boolean subspecies, Vector2f scrollSpeed) {
      super(renderer, textureSuffix, glow, blending, subspecies);
      this.scrollSpeed = scrollSpeed;
   }
}
