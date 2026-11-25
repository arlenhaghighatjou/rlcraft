package boni.dummy.client;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import boni.dummy.EntityDpsFloatingNumber;
import boni.dummy.EntityFloatingNumber;
import java.text.DecimalFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class RenderFloatingNumber extends Render<EntityFloatingNumber> {
   private static FontRenderer fontRenderer;
   private static DecimalFormat df = new DecimalFormat("#.##");

   public RenderFloatingNumber(RenderManager renderManager) {
      super(renderManager);
   }

   public void doRender(EntityFloatingNumber entity, double x, double y, double z, float entityYaw, float partialTicks) {
      boolean dps = entity instanceof EntityDpsFloatingNumber;
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);
      EntityPlayer player = Minecraft.getMinecraft().player;
      double xd = player.posX - entity.posX;
      double yd = player.posY - entity.posY;
      double zd = player.posZ - entity.posZ;
      double l = Math.sqrt(xd * xd + yd * yd + zd * zd);
      double scale = 0.01D * l;
      if (dps) {
         scale += 0.029999999329447746D;
      }

      GlStateManager.scale(-scale, -scale, scale);
      GlStateManager.translate(0.0D, -l / 10.0D, 0.0D);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(false);
      EaglercraftGPU.glNormal3f(0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      String s = df.format((double)(entity.damage / 2.0F));
      if (dps) {
         s = "DPS: " + s;
      }

      GlStateManager.translate((double)(-fontRenderer.getStringWidth(s) / 2), 0.0D, 0.0D);
      fontRenderer.drawString(s, 0.0F, 0.0F, -1, true);
      GlStateManager.translate((double)(fontRenderer.getStringWidth(s) / 2), 0.0D, 0.0D);
      GlStateManager.depthMask(true);
      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
   }

   protected ResourceLocation getEntityTexture(EntityFloatingNumber p_110775_1_) {
      return null;
   }

   static {
      fontRenderer = Minecraft.getMinecraft().fontRenderer;
   }
}
