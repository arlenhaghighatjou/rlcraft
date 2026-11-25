package org.orecruncher.dsurround.client.fx.particle.mote;

import javax.annotation.Nonnull;
import net.minecraft.util.ResourceLocation;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.lib.random.XorShiftRandom;

public enum RippleStyle {
   ORIGINAL("textures/particles/ripple.png"),
   CIRCLE("textures/particles/ripple1.png"),
   SQUARE("textures/particles/ripple2.png"),
   PIXELATED("textures/particles/pixel_ripples.png") {
      private final int FRAMES = 7;
      private final float DELTA;

      {
         this.getClass();
         this.DELTA = 1.0F / 7.0F;
      }

      public float getU1(int age) {
         int t = age / 2;
         return (float)t * this.DELTA;
      }

      public float getU2(int age) {
         return this.getU1(age) + this.DELTA;
      }

      public boolean doScaling() {
         return false;
      }

      public int getMaxAge() {
         this.getClass();
         return 7 * 2;
      }
   };

   private final ResourceLocation resource;

   private RippleStyle(@Nonnull String texture) {
      this.resource = new ResourceLocation("dsurround", texture);
   }

   @Nonnull
   public ResourceLocation getTexture() {
      return this.resource;
   }

   public float getU1(int age) {
      return 0.0F;
   }

   public float getU2(int age) {
      return 1.0F;
   }

   public float getV1(int age) {
      return 0.0F;
   }

   public float getV2(int age) {
      return 1.0F;
   }

   public boolean doScaling() {
      return true;
   }

   public boolean doAlpha() {
      return true;
   }

   public int getMaxAge() {
      return 12 + XorShiftRandom.current().nextInt(8);
   }

   @Nonnull
   public static RippleStyle getStyle(int v) {
      return v >= values().length ? CIRCLE : values()[v];
   }

   @Nonnull
   public static RippleStyle get() {
      return getStyle(ModOptions.rain.rainRippleStyle);
   }
}
