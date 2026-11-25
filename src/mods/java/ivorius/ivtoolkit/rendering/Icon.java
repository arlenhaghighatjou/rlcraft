package ivorius.ivtoolkit.rendering;

import ivorius.ivtoolkit.math.IvMathHelper;

public class Icon {
   private float minU;
   private float maxU;
   private float minV;
   private float maxV;

   private Icon(float minU, float maxU, float minV, float maxV) {
      this.minU = minU;
      this.maxU = maxU;
      this.minV = minV;
      this.maxV = maxV;
   }

   public static Icon fromCoords(float minU, float maxU, float minV, float maxV) {
      return new Icon(minU, maxU, minV, maxV);
   }

   public static Icon fromSize(float minU, float sizeU, float minV, float sizeV) {
      return new Icon(minU, minU + sizeU, minV, minV + sizeV);
   }

   public float getMinU() {
      return this.minU;
   }

   public float getMaxU() {
      return this.maxU;
   }

   public float getMinV() {
      return this.minV;
   }

   public float getMaxV() {
      return this.maxV;
   }

   public float getU(float lerp) {
      return IvMathHelper.mix(this.minU, this.maxU, lerp);
   }

   public float getV(float lerp) {
      return IvMathHelper.mix(this.minV, this.maxV, lerp);
   }
}
