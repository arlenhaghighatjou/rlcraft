package com.creativemd.creativecore.client.rendering.face;

import com.creativemd.creativecore.common.utils.math.vec.VectorFan;
import java.util.List;

public enum FaceRenderType implements IFaceRenderType {
   INSIDE_RENDERED(true, false),
   INSIDE_NOT_RENDERED(false, false),
   OUTSIDE_RENDERED(true, true),
   OUTSIDE_NOT_RENDERD(false, true);

   private final boolean shouldBeRendered;
   private final boolean outside;

   private FaceRenderType(boolean shouldBeRendered, boolean outside) {
      this.shouldBeRendered = shouldBeRendered;
      this.outside = outside;
   }

   public boolean shouldRender() {
      return this.shouldBeRendered;
   }

   public boolean isOutside() {
      return this.outside;
   }

   public boolean hasCachedFans() {
      return false;
   }

   public List<VectorFan> getCachedFans() {
      return null;
   }

   public float getScale() {
      return 1.0F;
   }
}
