package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelFireSkull;
import com.github.alexthe666.iceandfire.client.model.ModelIceSkull;
import com.github.alexthe666.iceandfire.entity.EntityDragonSkull;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderDragonSkull extends RenderLiving {
   public static final ResourceLocation SKULL_FIRE = new ResourceLocation("iceandfire:textures/models/firedragon/fire_skeleton_4.png");
   public static final ResourceLocation SKULL_ICE = new ResourceLocation("iceandfire:textures/models/icedragon/ice_skeleton_4.png");
   public static final ModelFireSkull FIRE_SKULL_MODEL = new ModelFireSkull();
   public static final ModelIceSkull ICE_SKULL_MODEL = new ModelIceSkull();

   public RenderDragonSkull(RenderManager renderManager) {
      super(renderManager, FIRE_SKULL_MODEL, 0.3F);
   }

   protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
      if (par1EntityLivingBase instanceof EntityDragonSkull) {
         GL11.glScalef(((EntityDragonSkull)par1EntityLivingBase).getDragonSize(), 1.0F, ((EntityDragonSkull)par1EntityLivingBase).getDragonSize());
         GL11.glScalef(1.0F, -((EntityDragonSkull)par1EntityLivingBase).getDragonSize(), 1.0F);
         GL11.glRotatef(((EntityDragonSkull)par1EntityLivingBase).getYaw(), 0.0F, 1.0F, 0.0F);
         super.preRenderCallback(par1EntityLivingBase, par2);
      }

   }

   protected ResourceLocation getEntityTexture(Entity entity) {
      if (entity instanceof EntityDragonSkull) {
         return ((EntityDragonSkull)entity).getType() == 0 ? SKULL_FIRE : SKULL_ICE;
      } else {
         return null;
      }
   }
}
