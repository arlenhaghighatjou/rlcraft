package com.creativemd.creativecore.client.rendering.face;

import com.creativemd.creativecore.common.utils.math.vec.VectorFan;
import java.util.List;

public class CachedFaceRenderType implements IFaceRenderType {
   private final boolean shouldBeRendered;
   private final boolean outside;
   private final List<VectorFan> fans;
   private final float scale;

   public CachedFaceRenderType(List<VectorFan> fans, float scale, boolean shouldBeRendered, boolean outside) {
      this.fans = fans;
      this.scale = scale;
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
      return true;
   }

   public List<VectorFan> getCachedFans() {
      return this.fans;
   }

   public float getScale() {
      return this.scale;
   }
}
