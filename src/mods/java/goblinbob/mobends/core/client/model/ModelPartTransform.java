package goblinbob.mobends.core.client.model;

import goblinbob.mobends.core.math.SmoothOrientation;
import goblinbob.mobends.core.math.TransformUtils;
import goblinbob.mobends.core.math.matrix.IMat4x4d;
import goblinbob.mobends.core.math.vector.IVec3f;
import goblinbob.mobends.core.math.vector.Vec3f;
import goblinbob.mobends.core.util.GlHelper;
import net.minecraft.client.renderer.GlStateManager;

public class ModelPartTransform implements IModelPart {
   public Vec3f position;
   public Vec3f scale;
   public Vec3f offset;
   public SmoothOrientation rotation;
   public float offsetScale;
   public Vec3f globalOffset;
   public final ModelPartTransform parent;

   public ModelPartTransform(ModelPartTransform parent) {
      this.offsetScale = 1.0F;
      this.globalOffset = new Vec3f();
      this.position = new Vec3f();
      this.scale = new Vec3f(1.0F, 1.0F, 1.0F);
      this.offset = new Vec3f();
      this.rotation = new SmoothOrientation();
      this.parent = parent;
   }

   public ModelPartTransform() {
      this((ModelPartTransform)null);
   }

   public void renderPart(float scale) {
   }

   public void renderJustPart(float scale) {
   }

   public void update(float ticksPerFrame) {
      this.rotation.update(ticksPerFrame);
   }

   public IVec3f getPosition() {
      return this.position;
   }

   public IVec3f getScale() {
      return this.scale;
   }

   public IVec3f getOffset() {
      return this.offset;
   }

   public SmoothOrientation getRotation() {
      return this.rotation;
   }

   public float getOffsetScale() {
      return this.offsetScale;
   }

   public IVec3f getGlobalOffset() {
      return this.globalOffset;
   }

   public void syncUp(IModelPart part) {
      if (part != null) {
         this.position.set(part.getPosition());
         this.rotation.set(part.getRotation());
         this.offset.set(part.getOffset());
         this.scale.set(part.getScale());
         this.offsetScale = part.getOffsetScale();
      }
   }

   public boolean isShowing() {
      return true;
   }

   public void applyPreTransform(float scale) {
      if (this.globalOffset.x != 0.0F || this.globalOffset.y != 0.0F || this.globalOffset.z != 0.0F) {
         GlStateManager.translate(this.globalOffset.x * scale, this.globalOffset.y * scale, this.globalOffset.z * scale);
      }

   }

   public void applyPreTransform(float scale, IMat4x4d dest) {
      if (this.globalOffset.x != 0.0F || this.globalOffset.y != 0.0F || this.globalOffset.z != 0.0F) {
         TransformUtils.translate(dest, (double)(this.globalOffset.x * scale), (double)(this.globalOffset.y * scale), (double)(this.globalOffset.z * scale));
      }

   }

   public void applyLocalTransform(float scale) {
      if (this.position.x != 0.0F || this.position.y != 0.0F || this.position.z != 0.0F) {
         GlStateManager.translate(this.position.x * scale * this.offsetScale, this.position.y * scale * this.offsetScale, this.position.z * scale * this.offsetScale);
      }

      if (this.offset.x != 0.0F || this.offset.y != 0.0F || this.offset.z != 0.0F) {
         GlStateManager.translate(this.offset.x * scale * this.offsetScale, this.offset.y * scale * this.offsetScale, this.offset.z * scale * this.offsetScale);
      }

      GlHelper.rotate(this.rotation.getSmooth());
      if (this.scale.x != 0.0F || this.scale.y != 0.0F || this.scale.z != 0.0F) {
         GlStateManager.scale(this.scale.x, this.scale.y, this.scale.z);
      }

   }

   public void propagateTransform(float scale) {
      this.applyLocalTransform(scale);
   }

   public void applyPostTransform(float scale) {
   }

   public void setVisible(boolean showModel) {
   }

   public void applyLocalTransform(float scale, IMat4x4d matrix) {
      if (this.position.x != 0.0F || this.position.y != 0.0F || this.position.z != 0.0F) {
         TransformUtils.translate(matrix, (double)(this.position.x * scale), (double)(this.position.y * scale), (double)(this.position.z * scale));
      }

      if (this.offset.x != 0.0F || this.offset.y != 0.0F || this.offset.z != 0.0F) {
         TransformUtils.translate(matrix, (double)(this.offset.x * scale * this.offsetScale), (double)(this.offset.y * scale * this.offsetScale), (double)(this.offset.z * scale * this.offsetScale));
      }

      TransformUtils.rotate(matrix, this.rotation.getSmooth());
   }

   public IModelPart getParent() {
      return this.parent;
   }
}
