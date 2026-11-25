package org.orecruncher.dsurround.client.handlers.fog;

import javax.annotation.Nonnull;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class FogResult {
   public static final float DEFAULT_PLANE_SCALE = 0.75F;
   private int fogMode;
   private float start;
   private float end;

   public FogResult() {
      this.fogMode = 0;
      this.end = this.start = 0.0F;
   }

   public FogResult(int fogMode, float distance, float scale) {
      this.set(fogMode, distance, scale);
   }

   public FogResult(float start, float end) {
      this.set(start, end);
   }

   public FogResult(@Nonnull RenderFogEvent event) {
      this.set(event);
   }

   public void set(int fogMode, float distance, float scale) {
      this.fogMode = fogMode;
      this.start = fogMode < 0 ? 0.0F : distance * scale;
      this.end = distance;
   }

   public void set(float start, float end) {
      this.fogMode = 0;
      this.start = start;
      this.end = end;
   }

   public void set(@Nonnull RenderFogEvent event) {
      this.set(event.getFogMode(), event.getFarPlaneDistance(), 0.75F);
   }

   public int getFogMode() {
      return this.fogMode;
   }

   public float getStart() {
      return this.start;
   }

   public float getEnd() {
      return this.end;
   }

   public boolean isValid(@Nonnull RenderFogEvent event) {
      return this.end > this.start && event.getFogMode() == this.fogMode;
   }

   public String toString() {
      return String.format("[mode: %d, start: %f, end: %f]", this.fogMode, this.start, this.end);
   }
}
