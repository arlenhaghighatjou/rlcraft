package goblinbob.mobends.core.client.model;

import goblinbob.mobends.core.math.SmoothOrientation;
import goblinbob.mobends.core.math.matrix.IMat4x4d;
import goblinbob.mobends.core.math.vector.IVec3f;

public interface IModelPart {
   void applyPreTransform(float var1);

   void applyPreTransform(float var1, IMat4x4d var2);

   void applyLocalTransform(float var1);

   void applyLocalTransform(float var1, IMat4x4d var2);

   default void applyCharacterTransform(float scale) {
      this.applyPreTransform(scale);
      if (this.getParent() != null) {
         this.getParent().applyCharacterTransform(scale * this.getOffsetScale());
      }

      this.applyLocalTransform(scale);
   }

   default void applyCharacterTransform(float scale, IMat4x4d dest) {
      this.applyPreTransform(scale, dest);
      if (this.getParent() != null) {
         this.getParent().applyCharacterTransform(scale * this.getOffsetScale(), dest);
      }

      this.applyLocalTransform(scale, dest);
   }

   default void propagateTransform(float scale) {
      this.applyLocalTransform(scale);
      this.applyPostTransform(scale);
   }

   void applyPostTransform(float var1);

   void renderPart(float var1);

   void renderJustPart(float var1);

   void update(float var1);

   void syncUp(IModelPart var1);

   void setVisible(boolean var1);

   IVec3f getPosition();

   IVec3f getScale();

   IVec3f getOffset();

   SmoothOrientation getRotation();

   float getOffsetScale();

   IVec3f getGlobalOffset();

   IModelPart getParent();

   boolean isShowing();
}
