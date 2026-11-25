package goblinbob.mobends.standard.client.renderer.entity;

import goblinbob.mobends.core.client.model.ModelPartTransform;
import goblinbob.mobends.core.math.Quaternion;
import goblinbob.mobends.core.math.vector.IVec3f;
import goblinbob.mobends.core.math.vector.Vec3f;
import goblinbob.mobends.core.util.GUtil;
import goblinbob.mobends.core.util.IColorRead;
import goblinbob.mobends.standard.data.BipedEntityData;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Supplier;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import org.lwjgl.opengl.GL11;

public class SwordTrail {
   protected final Supplier<IColorRead> baseColor;
   protected LinkedList<SwordTrail.TrailPart> trailPartList = new LinkedList();

   public SwordTrail(Supplier<IColorRead> baseColor) {
      this.baseColor = baseColor;
   }

   public void reset() {
      this.trailPartList.clear();
   }

   public void render() {
      GlStateManager.depthFunc(515);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.disableCull();
      GlStateManager.disableLighting();
      GlStateManager.disableTexture2D();
      GL11.glHint(3152, 4354);
      GlStateManager.shadeModel(7425);
      GlStateManager.pushMatrix();
      GlStateManager.glBegin(7);
      Iterator<SwordTrail.TrailPart> it = this.trailPartList.iterator();

      for(boolean first = true; it.hasNext(); first = false) {
         SwordTrail.TrailPart part = (SwordTrail.TrailPart)it.next();
         part.draw(first, !it.hasNext());
      }

      GlStateManager.glEnd();
      GlStateManager.popMatrix();
      GlStateManager.enableTexture2D();
      GlStateManager.disableCull();
      GlStateManager.enableLighting();
   }

   public void add(BipedEntityData<?> entityData, float velocityX, float velocityY, float velocityZ) {
      EntityLivingBase entity = entityData.getEntity();
      EnumHandSide primaryHand = entity.getPrimaryHand();
      SwordTrail.TrailPart newPart = new SwordTrail.TrailPart(primaryHand, (IColorRead)this.baseColor.get(), velocityX, velocityY, velocityZ);
      newPart.body.syncUp(entityData.body);
      if (primaryHand == EnumHandSide.RIGHT) {
         newPart.arm.syncUp(entityData.rightArm);
         newPart.foreArm.syncUp(entityData.rightForeArm);
         newPart.itemRotation.set(entityData.renderRightItemRotation.getSmooth());
      } else {
         newPart.arm.syncUp(entityData.leftArm);
         newPart.foreArm.syncUp(entityData.leftForeArm);
         newPart.itemRotation.set(entityData.renderLeftItemRotation.getSmooth());
      }

      newPart.renderOffset.set(entityData.globalOffset.getX(), entityData.globalOffset.getY(), entityData.globalOffset.getZ());
      newPart.renderRotation.set(entityData.renderRotation.getSmooth());
      newPart.renderRotation.negate();
      this.trailPartList.add(newPart);
   }

   public void add(BipedEntityData<?> entityData) {
      this.add(entityData, 0.0F, 0.0F, 0.0F);
   }

   public void update(float ticksPerFrame) {
      Iterator<SwordTrail.TrailPart> it = this.trailPartList.iterator();

      while(it.hasNext()) {
         SwordTrail.TrailPart trailPart = (SwordTrail.TrailPart)it.next();
         trailPart.update(ticksPerFrame);
         if (trailPart.ticksExisted > 20.0F) {
            it.remove();
         }
      }

   }

   protected static class TrailPart {
      protected EnumHandSide primaryHand;
      protected IColorRead baseColor;
      protected ModelPartTransform body = new ModelPartTransform();
      protected ModelPartTransform arm = new ModelPartTransform();
      protected ModelPartTransform foreArm = new ModelPartTransform();
      protected Quaternion renderRotation = new Quaternion();
      protected Vec3f renderOffset = new Vec3f();
      protected Quaternion itemRotation = new Quaternion();
      protected Vec3f position = new Vec3f();
      protected float velocityX;
      protected float velocityY;
      protected float velocityZ;
      protected float ticksExisted = 0.0F;

      public TrailPart(EnumHandSide primaryHand, IColorRead baseColor, float velocityX, float velocityY, float velocityZ) {
         this.primaryHand = primaryHand;
         this.baseColor = baseColor;
         this.velocityX = velocityX;
         this.velocityY = velocityY;
         this.velocityZ = velocityZ;
      }

      public void update(float ticksPerFrame) {
         this.ticksExisted += ticksPerFrame;
         this.position.x += this.velocityX * ticksPerFrame;
         this.position.y += this.velocityY * ticksPerFrame;
         this.position.z += this.velocityZ * ticksPerFrame;
      }

      public void draw(boolean first, boolean last) {
         float alpha = this.ticksExisted / 5.0F;
         alpha = Math.min(alpha, 1.0F);
         alpha = 1.0F - alpha;
         Vec3f[] points = new Vec3f[]{new Vec3f(0.0F, 0.0F, -8.0F + 8.0F * alpha), new Vec3f(0.0F, 0.0F, -8.0F - 8.0F * alpha)};
         GUtil.translate((IVec3f[])points, 0.0F, 0.0F, 16.0F);
         GUtil.rotate(points, this.itemRotation);
         GUtil.translate((IVec3f[])points, this.primaryHand == EnumHandSide.LEFT ? 1.0F : -1.0F, -6.0F, 0.0F);
         GUtil.rotate(points, this.foreArm.rotation.getSmooth());
         GUtil.translate((IVec3f[])points, 0.0F, -4.0F, 0.0F);
         GUtil.rotate(points, this.arm.rotation.getSmooth());
         GUtil.translate((IVec3f[])points, this.arm.position.x, 10.0F, 0.0F);
         GUtil.rotate(points, this.body.rotation.getSmooth());
         GUtil.translate((IVec3f[])points, 0.0F, 12.0F, 0.0F);
         GUtil.rotate(points, this.renderRotation);
         GUtil.translate((IVec3f[])points, this.renderOffset.x, this.renderOffset.y, this.renderOffset.z);

         for(Vec3f point : points) {
            point.add(this.position);
         }

         GlStateManager.color(this.baseColor.getR(), this.baseColor.getG(), this.baseColor.getB(), alpha);
         if (!first) {
            GlStateManager.glVertex3f(points[1].x, points[1].y, points[1].z);
            GlStateManager.glVertex3f(points[0].x, points[0].y, points[0].z);
         }

         GlStateManager.glVertex3f(points[0].x, points[0].y, points[0].z);
         GlStateManager.glVertex3f(points[1].x, points[1].y, points[1].z);
         if (last) {
            GlStateManager.glVertex3f(points[1].x, points[1].y, points[1].z);
            GlStateManager.glVertex3f(points[0].x, points[0].y, points[0].z);
         }

      }
   }
}
