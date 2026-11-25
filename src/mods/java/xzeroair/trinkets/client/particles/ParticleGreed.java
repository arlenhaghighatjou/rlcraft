package xzeroair.trinkets.client.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleGreed extends Particle {
   private static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B).addElement(DefaultVertexFormats.PADDING_1B);
   private boolean depth = false;

   public ParticleGreed(World world, double x, double y, double z, float r, float g, float b, float a) {
      super(world, x, y, z);
      this.particleRed = r;
      this.particleGreen = g;
      this.particleBlue = b;
      this.particleAlpha = a;
      this.depth = false;
      this.particleMaxAge = 16;
      this.particleAge = 0;
   }

   public ParticleGreed(World world, BlockPos pos, float r, float g, float b, float a) {
      super(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
      this.particleRed = r;
      this.particleGreen = g;
      this.particleBlue = b;
      this.particleAlpha = a;
      this.depth = false;
      this.particleMaxAge = 16;
      this.particleAge = 0;
   }

   public ParticleGreed(World world, Vec3d vec, int color, float a, boolean bool) {
      super(world, vec.x, vec.y, vec.z);
      int r = (color & 16711680) >> 16;
      int g = (color & '\uff00') >> 8;
      int b = (color & 255) >> 0;
      this.particleRed = (float)r / 255.0F;
      this.particleGreen = (float)g / 255.0F;
      this.particleBlue = (float)b / 255.0F;
      this.particleAlpha = a;
      this.depth = bool;
      this.particleMaxAge = 16;
      this.particleAge = 0;
   }

   public ParticleGreed(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, float colorRed, float colorGreen, float colorBlue) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.particleRed = colorRed;
      this.particleGreen = colorGreen;
      this.particleBlue = colorBlue;
      this.particleAlpha = 1.0F;
      this.particleMaxAge = 16;
      this.particleAge = 0;
   }

   public void renderParticle(WorldRenderer buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      GlStateManager.pushMatrix();
      GlStateManager.enableAlpha();
      if (this.depth) {
         ;
      }

      if (!this.depth) {
         GlStateManager.disableDepth();
      }

      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("xat:textures/particle/greed.png"));
      int i = (int)(((float)this.particleAge + partialTicks) * 15.0F / (float)this.particleMaxAge);
      if (i <= 15) {
         float f = (float)(i % 16) / 16.0F;
         float f1 = f + 0.0625F;
         float f2 = (float)(i / 16) / 16.0F;
         float f3 = f2 + 0.0625F;
         float f4 = 0.25F;
         float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
         float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
         float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
         int lmv = 0;
         int lmv2 = 240;
         buffer.begin(7, VERTEX_FORMAT);
         buffer.pos((double)(f5 - rotationX * 0.25F - rotationXY * 0.25F), (double)(f6 - rotationZ * 0.25F), (double)(f7 - rotationYZ * 0.25F - rotationXZ * 0.25F)).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
         buffer.pos((double)(f5 - rotationX * 0.25F + rotationXY * 0.25F), (double)(f6 + rotationZ * 0.25F), (double)(f7 - rotationYZ * 0.25F + rotationXZ * 0.25F)).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
         buffer.pos((double)(f5 + rotationX * 0.25F + rotationXY * 0.25F), (double)(f6 + rotationZ * 0.25F), (double)(f7 + rotationYZ * 0.25F + rotationXZ * 0.25F)).tex((double)f, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
         buffer.pos((double)(f5 + rotationX * 0.25F - rotationXY * 0.25F), (double)(f6 - rotationZ * 0.25F), (double)(f7 + rotationYZ * 0.25F - rotationXZ * 0.25F)).tex((double)f, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
         Tessellator.getInstance().draw();
         if (!this.depth) {
            GlStateManager.enableDepth();
         }

         if (this.depth) {
            ;
         }

         GlStateManager.disableAlpha();
         GlStateManager.popMatrix();
      }

   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setExpired();
      }

   }

   public int getFXLayer() {
      return 3;
   }
}
