package com.yyon.grapplinghook.entities;

import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.controllers.SegmentHandler;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGrappleArrow<T extends Entity> extends Render<T> {
   protected final Item item;
   private final RenderItem itemRenderer;
   private static final ResourceLocation LEASH_KNOT_TEXTURES = new ResourceLocation("grapplemod", "textures/entity/rope.png");

   public RenderGrappleArrow(RenderManager renderManagerIn, Item itemIn, RenderItem itemRendererIn) {
      super(renderManagerIn);
      this.item = itemIn;
      this.itemRenderer = itemRendererIn;
   }

   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      grappleArrow arrow = (grappleArrow)entity;
      if (arrow != null && !arrow.isDead) {
         SegmentHandler segmenthandler = arrow.segmenthandler;
         EntityLivingBase e = (EntityLivingBase)arrow.shootingEntity;
         if (e != null && !e.isDead) {
            int primaryhand = e.getPrimaryHand() == EnumHandSide.RIGHT ? 1 : -1;
            Vec3d offset = new Vec3d(0.0D, 0.0D, 0.0D);
            if (!arrow.attached) {
               if (arrow.righthand) {
                  offset = new Vec3d((double)primaryhand * -0.36D, -0.175D, 0.45D);
               } else {
                  offset = new Vec3d((double)primaryhand * 0.36D, -0.175D, 0.45D);
               }

               offset = offset.rotatePitch(-(e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * partialTicks) * 0.017453292F);
               offset = offset.rotateYaw(-(e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * partialTicks) * 0.017453292F);
               double dist = (double)e.getDistance(arrow);
               double mult = 1.0D - dist / 10.0D;
               if (mult <= 0.0D) {
                  offset = new Vec3d(0.0D, 0.0D, 0.0D);
               } else {
                  offset = new Vec3d(offset.x * mult, offset.y * mult, offset.z * mult);
                  x += offset.x;
                  y += offset.y;
                  z += offset.z;
               }
            }

            int k = e.getPrimaryHand() == EnumHandSide.RIGHT ? 1 : -1;
            float f7 = e.getSwingProgress(partialTicks);
            float f8 = MathHelper.sin(MathHelper.sqrt(f7) * 3.1415927F);
            float f9 = (e.prevRenderYawOffset + (e.renderYawOffset - e.prevRenderYawOffset) * partialTicks) * 0.017453292F;
            double d0 = (double)MathHelper.sin(f9);
            double d1 = (double)MathHelper.cos(f9);
            double d2 = (double)k * 0.35D;
            double d4;
            double d5;
            double d6;
            double d7;
            if ((this.renderManager.options == null || this.renderManager.options.thirdPersonView <= 0) && e == Minecraft.getMinecraft().player) {
               Vec3d vec3d;
               if (arrow.righthand) {
                  vec3d = new Vec3d((double)k * -0.36D, -0.175D, 0.45D);
               } else {
                  vec3d = new Vec3d((double)k * 0.36D, -0.175D, 0.45D);
               }

               vec3d = vec3d.rotatePitch(-(e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * partialTicks) * 0.017453292F);
               vec3d = vec3d.rotateYaw(-(e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * partialTicks) * 0.017453292F);
               vec3d = vec3d.rotateYaw(f8 * 0.5F);
               vec3d = vec3d.rotatePitch(-f8 * 0.7F);
               d4 = e.prevPosX + (e.posX - e.prevPosX) * (double)partialTicks + vec3d.x;
               d5 = e.prevPosY + (e.posY - e.prevPosY) * (double)partialTicks + vec3d.y;
               d6 = e.prevPosZ + (e.posZ - e.prevPosZ) * (double)partialTicks + vec3d.z;
               d7 = (double)e.getEyeHeight();
            } else {
               d4 = e.prevPosX + (e.posX - e.prevPosX) * (double)partialTicks - d1 * d2 - d0 * 0.8D;
               d5 = e.prevPosY + (double)e.getEyeHeight() + (e.posY - e.prevPosY) * (double)partialTicks - 0.45D;
               d6 = e.prevPosZ + (e.posZ - e.prevPosZ) * (double)partialTicks - d0 * d2 + d1 * 0.8D;
               d7 = e.isSneaking() ? -0.1875D : 0.0D;
            }

            double d13 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
            double d8 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks;
            double d9 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
            double d10 = (double)((float)(d4 - d13)) - offset.x;
            double d11 = (double)((float)(d5 - d8)) + d7 - offset.y;
            double d12 = (double)((float)(d6 - d9)) - offset.z;
            vec thispos = new vec(x, y, z);
            vec handpos = new vec(d10 + x, d11 + y, d12 + z);
            vec somethingpos = (new vec(d13, d8, d9)).sub(thispos);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            this.bindTexture(this.getEntityTexture(entity));
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer vertexbuffer = tessellator.getBuffer();
            if (segmenthandler == null) {
               this.drawSegment(thispos, handpos, arrow.taut, tessellator, vertexbuffer);
            } else {
               for(int i = 0; i < segmenthandler.segments.size() - 1; ++i) {
                  vec from = ((vec)segmenthandler.segments.get(i)).sub(somethingpos);
                  vec to = ((vec)segmenthandler.segments.get(i + 1)).sub(somethingpos);
                  if (i == 0) {
                     from = thispos;
                  }

                  if (i + 2 == segmenthandler.segments.size()) {
                     to = handpos;
                  }

                  double taut = 1.0D;
                  if (i == segmenthandler.segments.size() - 2) {
                     ;
                  }

                  this.drawSegment(from, to, taut, tessellator, vertexbuffer);
               }
            }

            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x, (float)y, (float)z);
            GlStateManager.enableRescaleNormal();
            GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            this.itemRenderer.renderItem(this.getStackToRender(entity), TransformType.GROUND);
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
         }
      }
   }

   public void drawDebug(vec point, Tessellator tessellator, WorldRenderer vertexbuffer, float R, float G, float B) {
      double X = point.x;
      double Y = point.y;
      double Z = point.z;
      vertexbuffer.begin(5, DefaultVertexFormats.POSITION_COLOR);
      vertexbuffer.pos(X, Y + 0.025D, Z).color(R, G, B, 1.0F).endVertex();
      vertexbuffer.pos(X - 0.025D, Y, Z - 0.025D).color(R, G, B, 1.0F).endVertex();
      vertexbuffer.pos(X, Y + 2.0D + 0.025D, Z).color(R, G, B, 1.0F).endVertex();
      vertexbuffer.pos(X - 0.025D, Y + 2.0D, Z - 0.025D).color(R, G, B, 1.0F).endVertex();
      tessellator.draw();
      vertexbuffer.begin(5, DefaultVertexFormats.POSITION_COLOR);
      vertexbuffer.pos(X + 0.025D, Y, Z - 0.025D).color(R, G, B, 1.0F).endVertex();
      vertexbuffer.pos(X, Y + 0.025D, Z).color(R, G, B, 1.0F).endVertex();
      vertexbuffer.pos(X + 0.025D, Y + 2.0D, Z - 0.025D).color(R, G, B, 1.0F).endVertex();
      vertexbuffer.pos(X, Y + 2.0D + 0.025D, Z).color(R, G, B, 1.0F).endVertex();
      tessellator.draw();
      vertexbuffer.begin(5, DefaultVertexFormats.POSITION_COLOR);
      vertexbuffer.pos(X, Y - 0.025D, Z).color(R, G, B, 1.0F).endVertex();
      vertexbuffer.pos(X + 0.025D, Y, Z - 0.025D).color(R, G, B, 1.0F).endVertex();
      vertexbuffer.pos(X, Y + 2.0D - 0.025D, Z).color(R, G, B, 1.0F).endVertex();
      vertexbuffer.pos(X + 0.025D, Y + 2.0D, Z - 0.025D).color(R, G, B, 1.0F).endVertex();
      tessellator.draw();
      vertexbuffer.begin(5, DefaultVertexFormats.POSITION_COLOR);
      vertexbuffer.pos(X - 0.025D, Y, Z - 0.025D).color(R, G, B, 1.0F).endVertex();
      vertexbuffer.pos(X, Y - 0.025D, Z).color(R, G, B, 1.0F).endVertex();
      vertexbuffer.pos(X - 0.025D, Y + 2.0D, Z - 0.025D).color(R, G, B, 1.0F).endVertex();
      vertexbuffer.pos(X, Y + 2.0D - 0.025D, Z).color(R, G, B, 1.0F).endVertex();
      tessellator.draw();
   }

   public void drawSegment(vec start, vec finish, double taut, Tessellator tessellator, WorldRenderer vertexbuffer) {
      if (start.sub(finish).length() >= 0.05D) {
         double x = start.x;
         double y = start.y;
         double z = start.z;
         double d10 = finish.x - x;
         double d11 = finish.y - y;
         double d12 = finish.z - z;
         vec forward = finish.sub(start).changelen(1.0D);
         vec up = forward.cross(new vec(1.0D, 0.0D, 0.0D));
         if (up.length() == 0.0D) {
            up = new vec(1.0D, 0.0D, 0.0D);
         }

         up.changelen_ip(0.025D);
         vec side = forward.cross(up);
         side.changelen_ip(0.025D);
         vec[] corners = new vec[]{up.mult(-1.0D).add(side.mult(-1.0D)), up.add(side.mult(-1.0D)), up.add(side), up.mult(-1.0D).add(side)};

         for(int corner = 0; corner < 4; ++corner) {
            vertexbuffer.begin(5, DefaultVertexFormats.POSITION_TEX);
            vec corner1 = corners[corner];
            vec corner2 = corners[(corner + 1) % 4];

            for(int i1 = 0; i1 <= 16; ++i1) {
               float f10 = (float)i1 / 16.0F;
               double X = x + d10 * (double)f10;
               double Z = z + d12 * (double)f10;
               double Y = y + d11 * (double)f10 - (1.0D - taut) * (0.25D - Math.pow((double)f10 - 0.5D, 2.0D)) * 1.5D;
               vertexbuffer.pos(X + corner1.x, Y + corner1.y, Z + corner1.z).tex(0.0D, (double)((float)i1 / 15.0F)).endVertex();
               vertexbuffer.pos(X + corner2.x, Y + corner2.y, Z + corner2.z).tex(1.0D, (double)((float)i1 / 15.0F)).endVertex();
            }

            tessellator.draw();
         }

      }
   }

   public boolean shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ) {
      return true;
   }

   public ItemStack getStackToRender(T entityIn) {
      return new ItemStack(this.item);
   }

   protected ResourceLocation getEntityTexture(Entity entity) {
      return LEASH_KNOT_TEXTURES;
   }
}
