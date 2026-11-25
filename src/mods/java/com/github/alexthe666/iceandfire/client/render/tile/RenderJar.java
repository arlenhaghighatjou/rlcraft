package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelPixie;
import com.github.alexthe666.iceandfire.client.render.entity.RenderPixie;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityJar;
import net.ilexiconn.llibrary.client.util.ItemTESRContext;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;

public class RenderJar extends TileEntitySpecialRenderer<TileEntityJar> {
   private static final ModelPixie MODEL_PIXIE = new ModelPixie();

   public RenderJar() {
   }

   public void render(TileEntityJar entity, double x, double y, double z, float f, int f1, float alpha) {
      int meta = 0;
      boolean hasPixie = false;
      if (entity != null && entity.getWorld() != null) {
         meta = entity.pixieType;
         hasPixie = entity.hasPixie;
      } else if (ItemTESRContext.INSTANCE.getCurrentStack() != null) {
         hasPixie = ItemTESRContext.INSTANCE.getCurrentStack().getItemDamage() != 0;
         meta = ItemTESRContext.INSTANCE.getCurrentStack().getItemDamage() - 1;
      }

      if (hasPixie) {
         GL11.glPushMatrix();
         GL11.glTranslatef((float)x + 0.5F, (float)y + 1.501F, (float)z + 0.5F);
         GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
         GL11.glPushMatrix();
         switch(meta) {
         case 1:
            this.bindTexture(RenderPixie.TEXTURE_1);
            break;
         case 2:
            this.bindTexture(RenderPixie.TEXTURE_2);
            break;
         case 3:
            this.bindTexture(RenderPixie.TEXTURE_3);
            break;
         case 4:
            this.bindTexture(RenderPixie.TEXTURE_4);
            break;
         case 5:
            this.bindTexture(RenderPixie.TEXTURE_5);
            break;
         default:
            this.bindTexture(RenderPixie.TEXTURE_0);
         }

         if (entity != null && entity.getWorld() != null) {
            if (entity.hasProduced) {
               GL11.glTranslatef(0.0F, 0.9F, 0.0F);
            } else {
               GL11.glTranslatef(0.0F, 0.6F, 0.0F);
            }

            GL11.glDisable(2884);
            GlStateManager.rotate(this.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, f), 0.0F, 1.0F, 0.0F);
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
            GlStateManager.disableLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            GlStateManager.enableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            MODEL_PIXIE.animateInJar(entity.hasProduced, entity, 0.0F);
            GL11.glEnable(2884);
         }

         GL11.glPopMatrix();
         GL11.glPopMatrix();
      }

   }

   protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
      float f;
      for(f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F) {
         ;
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return prevYawOffset + partialTicks * f;
   }
}
