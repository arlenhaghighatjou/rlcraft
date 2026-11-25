package com.creativemd.creativecore.client.rendering;

public enum RenderBox$SideRenderType {
   INSIDE_RENDERED(true, false),
   INSIDE_NOT_RENDERED(false, false),
   OUTSIDE_RENDERED(true, true),
   OUTSIDE_NOT_RENDERD(false, true);

   public final boolean shouldBeRendered;
   public final boolean outside;

   private RenderBox$SideRenderType(boolean shouldBeRendered, boolean outside) {
      this.shouldBeRendered = shouldBeRendered;
      this.outside = outside;
   }
}
