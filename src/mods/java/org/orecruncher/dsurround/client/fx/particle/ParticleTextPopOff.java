package org.orecruncher.dsurround.client.fx.particle;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.Color;
import org.orecruncher.lib.gfx.OpenGlState;

@SideOnly(Side.CLIENT)
public class ParticleTextPopOff extends ParticleBase {
   protected static final float GRAVITY = 0.8F;
   protected static final float SIZE = 3.0F;
   protected static final int LIFESPAN = 12;
   protected static final double BOUNCE_STRENGTH = 1.5D;
   protected static final int SHADOW_COLOR = Color.BLACK.rgbWithAlpha(1.0F);
   protected int renderColor;
   protected boolean grow;
   protected String text;
   protected float drawX;
   protected float drawY;

   public ParticleTextPopOff(World world, String text, Color color, double x, double y, double z) {
      this(world, text, color, x, y, z, 0.001D, 0.07500000000000001D, 0.001D);
   }

   public ParticleTextPopOff(World world, String text, Color color, double x, double y, double z, double dX, double dY, double dZ) {
      super(world, x, y, z, dX, dY, dZ);
      this.renderColor = Color.WHITE.rgbWithAlpha(1.0F);
      this.grow = true;
      this.renderColor = color.rgbWithAlpha(1.0F);
      this.motionX = dX;
      this.motionY = dY;
      this.motionZ = dZ;
      float dist = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
      this.motionX = this.motionX / (double)dist * 0.12D;
      this.motionY = this.motionY / (double)dist * 0.12D;
      this.motionZ = this.motionZ / (double)dist * 0.12D;
      this.particleTextureJitterX = 1.5F;
      this.particleTextureJitterY = 1.5F;
      this.particleGravity = 0.8F;
      this.particleScale = 3.0F;
      this.particleMaxAge = 12;
      this.setText(text);
   }

   public ParticleTextPopOff setText(@Nonnull String text) {
      this.text = text;
      this.drawX = (float)(-MathHelper.floor((float)this.font.getStringWidth(this.text) / 2.0F) + 1);
      this.drawY = (float)(-MathHelper.floor((float)this.font.FONT_HEIGHT / 2.0F) + 1);
      return this;
   }

   public ParticleTextPopOff setColor(@Nonnull Color color) {
      this.renderColor = color.rgbWithAlpha(1.0F);
      return this;
   }

   public void renderParticle(@Nonnull BufferBuilder worldRendererIn, @Nonnull Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float pitch = this.manager.playerViewX * (float)(this.isThirdPersonView() ? -1 : 1);
      float yaw = -this.manager.playerViewY;
      float locX = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - this.interpX());
      float locY = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - this.interpY());
      float locZ = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - this.interpZ());
      OpenGlState glState = OpenGlState.push();
      GlStateManager.translate(locX, locY, locZ);
      GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      GlStateManager.scale((double)this.particleScale * 0.008D, (double)this.particleScale * 0.008D, (double)this.particleScale * 0.008D);
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.003662109F);
      this.font.drawString(this.text, this.drawX, this.drawY, SHADOW_COLOR, false);
      GlStateManager.translate(-0.3F, -0.3F, -0.001F);
      this.font.drawString(this.text, this.drawX, this.drawY, this.renderColor, false);
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, OpenGlHelper.lastBrightnessX, OpenGlHelper.lastBrightnessY);
      OpenGlState.pop(glState);
      if (this.grow) {
         this.particleScale *= 1.08F;
         if ((double)this.particleScale > 9.0D) {
            this.grow = false;
         }
      } else {
         this.particleScale *= 0.96F;
      }

   }

   public boolean shouldDisableDepth() {
      return true;
   }

   public int getFXLayer() {
      return 3;
   }
}
