package xzeroair.trinkets.client.particles;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.particle.Particle;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.helpers.RayTraceHelper;

@SideOnly(Side.CLIENT)
public class ParticleSmell extends Particle {
   private static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B).addElement(DefaultVertexFormats.PADDING_1B);
   private Vec3d start;
   private Vec3d end;
   private boolean depth = false;
   private RayTraceHelper.Beam beam;
   List<Vec3d> points = new ArrayList();
   Vec3d firstSegment;
   Vec3d secondSegment;
   Vec3d thirdSegment;
   private boolean first = false;

   public ParticleSmell(World world, double x, double y, double z, float r, float g, float b, float a) {
      super(world, x, y, z);
      this.particleRed = r;
      this.particleGreen = g;
      this.particleBlue = b;
      this.particleAlpha = a;
      this.depth = false;
      this.particleMaxAge = 16;
      this.particleAge = 0;
   }

   public ParticleSmell(World world, BlockPos pos, float r, float g, float b, float a) {
      super(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
      this.particleRed = r;
      this.particleGreen = g;
      this.particleBlue = b;
      this.particleAlpha = a;
      this.depth = false;
      this.particleMaxAge = 16;
      this.particleAge = 0;
   }

   public ParticleSmell(World world, Vec3d start, Vec3d end, int color, float a, boolean bool) {
      super(world, start.x, start.y, start.z);
      int r = (color & 16711680) >> 16;
      int g = (color & '\uff00') >> 8;
      int b = (color & 255) >> 0;
      this.particleRed = (float)r / 255.0F;
      this.particleGreen = (float)g / 255.0F;
      this.particleBlue = (float)b / 255.0F;
      this.particleAlpha = a;
      this.depth = bool;
      this.particleMaxAge = 1;
      this.particleAge = 0;
      this.start = start;
      this.end = end;
   }

   public ParticleSmell(World world, Vec3d start, Vec3d end, RayTraceHelper.Beam beam, int color, float a, boolean bool) {
      super(world, start.x, start.y, start.z);
      int r = (color & 16711680) >> 16;
      int g = (color & '\uff00') >> 8;
      int b = (color & 255) >> 0;
      this.particleRed = (float)r / 255.0F;
      this.particleGreen = (float)g / 255.0F;
      this.particleBlue = (float)b / 255.0F;
      this.particleAlpha = a;
      this.depth = bool;
      int rf = TrinketsConfig.CLIENT.items.DRAGON_EYE.Render_Cooldown;
      this.particleMaxAge = 16;
      this.particleAge = 0;
      this.start = start;
      this.end = end;
      this.beam = beam;
   }

   public void renderParticle(WorldRenderer buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float r = this.particleRed;
      float g = this.particleGreen;
      float b = this.particleBlue;
      float a = this.particleAlpha;
      if (this.depth) {
         ;
      }

      int i = (int)(((float)this.particleAge + partialTicks) * 15.0F / (float)this.particleMaxAge);
      if (i <= 15) {
         GlStateManager.pushMatrix();
         float f = (float)(i % 16) / 16.0F;
         float f1 = f + 0.0625F;
         float f2 = (float)(i / 16) / 16.0F;
         float f3 = f2 + 0.0625F;
         float f4 = 0.25F;
         float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * 0.5D - interpPosX);
         float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * 0.5D - interpPosY);
         float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * 0.5D - interpPosZ);
         float f8 = (float)(this.end.x + (this.end.x - this.end.x) * 0.5D - interpPosX);
         float f9 = (float)(this.end.y + (this.end.y - this.end.y) * 0.5D - interpPosY);
         float f10 = (float)(this.end.z + (this.end.z - this.end.z) * 0.5D - interpPosZ);
         int lmv = 0;
         int lmv2 = 240;
         GlStateManager.disableTexture2D();
         GlStateManager.enableBlend();
         GlStateManager.enableAlpha();
         if (!this.depth) {
            GlStateManager.disableDepth();
         }

         buffer.begin(3, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
         GlStateManager.glLineWidth(6.0F);
         buffer.pos((double)f5, (double)f6, (double)f7).tex(0.0D, 0.0D).lightmap(0, 240).color(r, g, b, a).endVertex();
         buffer.pos((double)f8, (double)f9, (double)f10).tex(0.0D, 0.0D).lightmap(0, 240).color(r, g, b, a).endVertex();
         Tessellator.getInstance().draw();
         if (!this.depth) {
            GlStateManager.enableDepth();
         }

         GlStateManager.disableAlpha();
         GlStateManager.disableBlend();
         GlStateManager.enableTexture2D();
         GlStateManager.popMatrix();
      }

   }

   public void onUpdate() {
      if (this.posX != this.prevPosX || this.posY != this.prevPosY || this.posZ != this.prevPosZ) {
         this.setExpired();
      }

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
