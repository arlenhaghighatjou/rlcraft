package bettercombat.mod.client.particle;

import net.minecraft.client.particle.Particle;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntitySweepAttack2FX extends Particle {
   private static final ResourceLocation SWEEP_TEXTURE = new ResourceLocation("textures/entity/sweep.png");
   private static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B).addElement(DefaultVertexFormats.PADDING_1B);
   private int life;
   private int lifeTime;
   private TextureManager textureManager;
   private float size;

   public EntitySweepAttack2FX(TextureManager textureManager, World world, double x, double y, double z, double scale) {
      super(world, x, y, z, 0.0D, 0.0D, 0.0D);
      this.textureManager = textureManager;
      this.lifeTime = 4;
      this.particleRed = this.particleGreen = this.particleBlue = this.rand.nextFloat() * 0.6F + 0.4F;
      this.size = 1.0F - (float)scale * 0.5F;
   }

   public void renderParticle(WorldRenderer buf, Entity entityIn, float partialTicks, float rotationX, float rotZ, float rotYZ, float rotXY, float rotXZ) {
      int tInd = (int)(((float)this.life + partialTicks) * 3.0F / (float)this.lifeTime);
      if (tInd <= 7) {
         this.textureManager.bindTexture(SWEEP_TEXTURE);
         float u1 = (float)(tInd % 4) / 4.0F;
         float u2 = u1 - 0.24975F;
         float v1 = (float)tInd / 2.0F / 2.0F;
         float v2 = v1 - 0.4995F;
         float scale = -1.0F * this.size;
         float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
         float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
         float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableLighting();
         RenderHelper.disableStandardItemLighting();
         buf.begin(7, VERTEX_FORMAT);
         buf.pos((double)(x - rotationX * scale - rotXY * scale), (double)(y - rotZ * scale * 0.5F), (double)(z - rotYZ * scale - rotXZ * scale)).tex((double)u2, (double)v2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
         buf.pos((double)(x - rotationX * scale + rotXY * scale), (double)(y + rotZ * scale * 0.5F), (double)(z - rotYZ * scale + rotXZ * scale)).tex((double)u2, (double)v1).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
         buf.pos((double)(x + rotationX * scale + rotXY * scale), (double)(y + rotZ * scale * 0.5F), (double)(z + rotYZ * scale + rotXZ * scale)).tex((double)u1, (double)v1).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
         buf.pos((double)(x + rotationX * scale - rotXY * scale), (double)(y - rotZ * scale * 0.5F), (double)(z + rotYZ * scale - rotXZ * scale)).tex((double)u1, (double)v2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
         Tessellator.getInstance().draw();
         GlStateManager.enableLighting();
      }

   }

   public int getBrightnessForRender(float partTicks) {
      return 61680;
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      ++this.life;
      if (this.life == this.lifeTime) {
         this.setExpired();
      }

   }

   public int getFXLayer() {
      return 3;
   }
}
