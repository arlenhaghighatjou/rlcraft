package org.orecruncher.dsurround.client.aurora;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
final class Panel {
   private static final float COS_DEG90_FACTOR = MathStuff.cos(1.5707964F);
   private static final float COS_DEG270_FACTOR = MathStuff.cos(4.712389F);
   private static final float SIN_DEG90_FACTOR = MathStuff.sin(1.5707964F);
   private static final float SIN_DEG270_FACTOR = MathStuff.sin(4.712389F);
   public float dZ = 0.0F;
   public float dY = 0.0F;
   public float cosDeg90 = 0.0F;
   public float cosDeg270 = 0.0F;
   public float sinDeg90 = 0.0F;
   public float sinDeg270 = 0.0F;
   public float angle;
   public float posX;
   public float posY;
   public float posZ;
   public float tetX = 0.0F;
   public float tetX2 = 0.0F;
   public float tetZ = 0.0F;
   public float tetZ2 = 0.0F;

   public Panel(@Nonnull Panel template, int offset) {
      float rads = MathStuff.toRadians(90.0F + template.angle);
      this.posX = template.posX + MathStuff.cos(rads) * (float)offset;
      this.posY = template.posY - 2.0F;
      this.posZ = template.posZ + MathStuff.sin(rads) * (float)offset;
      this.angle = template.angle;
   }

   public Panel(float x, float y, float z, float theta) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      this.angle = theta;
   }

   public void setDeltaZ(float f) {
      this.dZ = f;
   }

   public void setDeltaY(float f) {
      this.dY = f;
   }

   public float getModdedZ() {
      return this.posZ + this.dZ;
   }

   public float getModdedY() {
      float y = this.posY + this.dY;
      return y < 0.0F ? 0.0F : y;
   }

   public void setWidth(float w) {
      this.cosDeg270 = COS_DEG270_FACTOR * w;
      this.cosDeg90 = COS_DEG90_FACTOR * w;
      this.sinDeg270 = SIN_DEG270_FACTOR * w;
      this.sinDeg90 = SIN_DEG90_FACTOR * w;
      this.tetX = this.posX + this.cosDeg90;
      this.tetX2 = this.posX + this.cosDeg270;
   }
}
