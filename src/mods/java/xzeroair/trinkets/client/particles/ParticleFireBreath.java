package xzeroair.trinkets.client.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleFlame;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleFireBreath extends ParticleFlame {
   public ParticleFireBreath(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int color, float scale) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      int r = (color & 16711680) >> 16;
      int g = (color & '\uff00') >> 8;
      int b = (color & 255) >> 0;
      this.particleRed = (float)r / 255.0F;
      this.particleGreen = (float)g / 255.0F;
      this.particleBlue = (float)b / 255.0F;
      this.particleAlpha = 1.0F;
      this.particleAge = 0;
      this.particleMaxAge = 31;
      this.particleScale = scale;
   }

   public void renderParticle(WorldRenderer buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float fT = ((float)this.particleAge + partialTicks) / (float)this.particleMaxAge;
      float flameScale = this.particleScale;
      int i = (int)(((float)this.particleAge + partialTicks) * 30.0F / (float)this.particleMaxAge);
      this.particleScale = flameScale * (1.0F - fT * fT * 0.5F);
      if (i <= this.particleMaxAge - 1) {
         GlStateManager.pushMatrix();
         Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("xat", "textures/particle/dragon_breath.png"));
         boolean flipU = false;
         boolean flipUV = false;
         int width = 16;
         int height = 16;
         float tileWidth = 0.00390625F;
         float tileHeight = 0.00390625F;
         int x = i < 16 ? i * 16 : (i + 1) * 16;
         int y = i < 16 ? 16 : 0;
         float uF = (float)x;
         float uF2 = (float)(x + 16);
         float vF = (float)y;
         float vF2 = (float)(y + 16);
         int offset = this.particleMaxAge;
         float f4 = this.particleScale;
         int b = this.getBrightnessForRender(partialTicks);
         int j = b >> 16 & '\uffff';
         int k = b & '\uffff';
         float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
         float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
         float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
         Vec3d[] avec3d = new Vec3d[]{new Vec3d((double)(-rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(-rotationYZ * f4 - rotationXZ * f4)), new Vec3d((double)(-rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(-rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double)(rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double)(rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(rotationYZ * f4 - rotationXZ * f4))};
         if (this.particleAngle != 0.0F) {
            float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
            float f9 = MathHelper.cos(f8 * 0.5F);
            float f10 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.x;
            float f11 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.y;
            float f12 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.z;
            Vec3d vec3d = new Vec3d((double)f10, (double)f11, (double)f12);

            for(int l = 0; l < 4; ++l) {
               avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale((double)(2.0F * f9)));
            }
         }

         buffer.pos((double)f5 + avec3d[0].x, (double)f6 + avec3d[0].y, (double)f7 + avec3d[0].z).tex((double)(uF * 0.00390625F), (double)(vF2 * 0.00390625F)).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
         buffer.pos((double)f5 + avec3d[1].x, (double)f6 + avec3d[1].y, (double)f7 + avec3d[1].z).tex((double)(uF2 * 0.00390625F), (double)(vF2 * 0.00390625F)).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
         buffer.pos((double)f5 + avec3d[2].x, (double)f6 + avec3d[2].y, (double)f7 + avec3d[2].z).tex((double)(uF2 * 0.00390625F), (double)(vF * 0.00390625F)).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
         buffer.pos((double)f5 + avec3d[3].x, (double)f6 + avec3d[3].y, (double)f7 + avec3d[3].z).tex((double)(uF * 0.00390625F), (double)(vF * 0.00390625F)).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
         GlStateManager.popMatrix();
      }

   }

   public void onUpdate() {
      super.onUpdate();
   }
}
