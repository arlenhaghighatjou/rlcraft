package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelGorgon;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGorgonEyes;
import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderGorgon extends RenderLiving<EntityGorgon> {
   public static final ResourceLocation PASSIVE_TEXTURE = new ResourceLocation("iceandfire:textures/models/gorgon/gorgon_passive.png");
   public static final ResourceLocation AGRESSIVE_TEXTURE = new ResourceLocation("iceandfire:textures/models/gorgon/gorgon_active.png");
   public static final ResourceLocation DEAD_TEXTURE = new ResourceLocation("iceandfire:textures/models/gorgon/gorgon_decapitated.png");

   public RenderGorgon(RenderManager renderManager) {
      super(renderManager, new ModelGorgon(), 0.6F);
      this.layerRenderers.add(new LayerGorgonEyes(this));
   }

   public void preRenderCallback(EntityGorgon entitylivingbaseIn, float partialTickTime) {
      GL11.glScalef(0.85F, 0.85F, 0.85F);
   }

   protected ResourceLocation getEntityTexture(EntityGorgon gorgon) {
      if (gorgon.getAnimation() == EntityGorgon.ANIMATION_SCARE) {
         return AGRESSIVE_TEXTURE;
      } else {
         return gorgon.deathTime > 0 ? DEAD_TEXTURE : PASSIVE_TEXTURE;
      }
   }
}
