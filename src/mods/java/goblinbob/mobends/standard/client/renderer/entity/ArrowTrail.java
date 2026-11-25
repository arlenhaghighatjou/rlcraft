package goblinbob.mobends.standard.client.renderer.entity;

import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.math.vector.Vec3f;
import goblinbob.mobends.core.math.vector.VectorUtils;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.Vec3d;

public class ArrowTrail {
   public static final int MAX_LENGTH = 10;
   public static final float SPAWN_INTERVAL = 1.0F;
   private final Minecraft mc = Minecraft.getMinecraft();
   private EntityArrow trackedArrow;
   private ArrowTrail.TrailNode[] nodes;
   private float spawnCooldown = 0.0F;

   public ArrowTrail(EntityArrow arrow) {
      this.trackedArrow = arrow;
      this.spawnCooldown = 1.0F;
      this.nodes = new ArrowTrail.TrailNode[10];
      this.resetNodes();
   }

   public void onRenderTick() {
      this.spawnCooldown += DataUpdateHandler.ticksPerFrame;
   }

   public void render(double x, double y, double z, float partialTicks) {
      if (this.spawnCooldown > 40.0F) {
         this.spawnCooldown = 0.0F;
         this.resetNodes();
      }

      while(this.spawnCooldown >= 1.0F) {
         for(int i = 9; i > 0; --i) {
            this.nodes[i].moveTo(this.nodes[i - 1]);
         }

         this.nodes[0].moveTo(this.trackedArrow);
         --this.spawnCooldown;
      }

      this.renderNodes(partialTicks);
   }

   public void resetNodes() {
      for(int i = 0; i < 10; ++i) {
         this.nodes[i] = new ArrowTrail.TrailNode(this.trackedArrow);
      }

   }

   public void renderNodes(float partialTicks) {
      Entity viewEntity = Minecraft.getMinecraft().getRenderViewEntity();
      if (viewEntity != null) {
         Vec3d viewPos = new Vec3d(viewEntity.prevPosX + (viewEntity.posX - viewEntity.prevPosX) * (double)partialTicks, viewEntity.prevPosY + (viewEntity.posY - viewEntity.prevPosY) * (double)partialTicks, viewEntity.prevPosZ + (viewEntity.posZ - viewEntity.prevPosZ) * (double)partialTicks);
         float r = 1.0F;
         float g = 1.0F;
         float b = 1.0F;
         float a = 0.5F;
         GlStateManager.color(r, g, b, a);
         GlStateManager.pushMatrix();
         GlStateManager.disableTexture2D();
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer vertexbuffer = tessellator.getBuffer();
         vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);

         for(int i = 1; i < 10; ++i) {
            ArrowTrail.TrailNode node0 = this.nodes[i - 1];
            ArrowTrail.TrailNode node1 = this.nodes[i];
            Vec3d pos0 = new Vec3d(node0.x - viewPos.x, node0.y - viewPos.y, node0.z - viewPos.z);
            Vec3d pos1 = new Vec3d(node1.x - viewPos.x, node1.y - viewPos.y, node1.z - viewPos.z);
            float scale0 = (float)(10 - i) / 10.0F * 0.1F;
            float scale1 = (10.0F - (float)i - 1.0F) / 10.0F * 0.1F;
            if (i == 1) {
               scale1 = 0.0F;
            }

            Vec3f up0 = node0.up;
            Vec3f right0 = node0.right;
            Vec3f up1 = node1.up;
            Vec3f right1 = node1.right;
            vertexbuffer.pos(pos0.x + (double)(-right0.x * scale0), pos0.y + (double)(-right0.y * scale0), pos0.z + (double)(-right0.z * scale0)).tex(0.0D, 0.15625D).endVertex();
            vertexbuffer.pos(pos0.x + (double)(right0.x * scale0), pos0.y + (double)(right0.y * scale0), pos0.z + (double)(right0.z * scale0)).tex(0.0D, 0.15625D).endVertex();
            vertexbuffer.pos(pos1.x + (double)(right1.x * scale1), pos1.y + (double)(right1.y * scale1), pos1.z + (double)(right1.z * scale1)).tex(0.0D, 0.15625D).endVertex();
            vertexbuffer.pos(pos1.x + (double)(-right1.x * scale1), pos1.y + (double)(-right1.y * scale1), pos1.z + (double)(-right1.z * scale1)).tex(0.0D, 0.15625D).endVertex();
            vertexbuffer.pos(pos0.x + (double)(-up0.x * scale0), pos0.y + (double)(-up0.y * scale0), pos0.z + (double)(-up0.z * scale0)).tex(0.0D, 0.15625D).endVertex();
            vertexbuffer.pos(pos0.x + (double)(up0.x * scale0), pos0.y + (double)(up0.y * scale0), pos0.z + (double)(up0.z * scale0)).tex(0.0D, 0.15625D).endVertex();
            vertexbuffer.pos(pos1.x + (double)(up1.x * scale1), pos1.y + (double)(up1.y * scale1), pos1.z + (double)(up1.z * scale1)).tex(0.0D, 0.15625D).endVertex();
            vertexbuffer.pos(pos1.x + (double)(-up1.x * scale1), pos1.y + (double)(-up1.y * scale1), pos1.z + (double)(-up1.z * scale1)).tex(0.0D, 0.15625D).endVertex();
         }

         tessellator.draw();
         GlStateManager.enableCull();
         GlStateManager.enableTexture2D();
         GlStateManager.enableLighting();
         GlStateManager.popMatrix();
      }
   }

   public void renderAxis(double x, double y, double z) {
      Vec3d forward = this.trackedArrow.getForward();
      forward = new Vec3d(-forward.x, -forward.y, forward.z);
      Vec3d up = Vec3d.fromPitchYaw(this.trackedArrow.rotationPitch + 90.0F, this.trackedArrow.rotationYaw);
      up = new Vec3d(-up.x, -up.y, up.z);
      Vec3d right = forward.crossProduct(up);
      GlStateManager.pushMatrix();
      GlStateManager.disableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      GlStateManager.depthFunc(519);
      GlStateManager.translate(x, y, z);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer vertexbuffer = tessellator.getBuffer();
      GlStateManager.color(1.0F, 0.0F, 0.0F, 1.0F);
      vertexbuffer.begin(1, DefaultVertexFormats.POSITION_TEX);
      vertexbuffer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.15625D).endVertex();
      vertexbuffer.pos(right.x, right.y, right.z).tex(0.15625D, 0.15625D).endVertex();
      tessellator.draw();
      GlStateManager.color(0.0F, 1.0F, 0.0F, 1.0F);
      vertexbuffer.begin(1, DefaultVertexFormats.POSITION_TEX);
      vertexbuffer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.15625D).endVertex();
      vertexbuffer.pos(up.x, up.y, up.z).tex(0.15625D, 0.15625D).endVertex();
      tessellator.draw();
      GlStateManager.color(0.0F, 0.0F, 1.0F, 1.0F);
      vertexbuffer.begin(1, DefaultVertexFormats.POSITION_TEX);
      vertexbuffer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.15625D).endVertex();
      vertexbuffer.pos(forward.x, forward.y, forward.z).tex(0.15625D, 0.15625D).endVertex();
      tessellator.draw();
      GlStateManager.depthFunc(515);
      GlStateManager.enableCull();
      GlStateManager.enableTexture2D();
      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
   }

   public boolean shouldBeRemoved() {
      return this.mc.world == null || this.trackedArrow.isDead;
   }

   static class TrailNode {
      public double x;
      public double y;
      public double z;
      public final Vec3f up = new Vec3f();
      public final Vec3f right = new Vec3f();

      TrailNode(EntityArrow arrow) {
         this.moveTo(arrow);
      }

      public void moveTo(ArrowTrail.TrailNode trailNode) {
         this.x = trailNode.x;
         this.y = trailNode.y;
         this.z = trailNode.z;
         this.up.set(trailNode.up);
         this.right.set(trailNode.right);
      }

      public void moveTo(EntityArrow arrow) {
         this.x = arrow.posX;
         this.y = arrow.posY;
         this.z = arrow.posZ;
         Vec3d forward = arrow.getForward();
         Vec3d up = Vec3d.fromPitchYaw(arrow.rotationPitch + 90.0F, arrow.rotationYaw);
         this.up.set((float)(-up.x), (float)(-up.y), (float)up.z);
         VectorUtils.cross((float)(-forward.x), (float)(-forward.y), (float)forward.z, this.up.x, this.up.y, this.up.z, this.right);
      }
   }
}
