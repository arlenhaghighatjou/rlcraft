package ivorius.ivtoolkit.models.textures;

import ivorius.ivtoolkit.models.utils.MathUtils;

public class TextureSub implements Texture {
   private Texture parent;
   private float minU;
   private float minV;
   private float maxU;
   private float maxV;

   public TextureSub(Texture parent, float minU, float minV, float maxU, float maxV) {
      this.parent = parent;
      this.minU = minU;
      this.minV = minV;
      this.maxU = maxU;
      this.maxV = maxV;
   }

   public void bindTexture() {
      this.parent.bindTexture();
   }

   public float minU() {
      return MathUtils.mix(this.parent.minU(), this.parent.maxU(), this.minU);
   }

   public float maxU() {
      return MathUtils.mix(this.parent.minU(), this.parent.maxU(), this.maxU);
   }

   public float minV() {
      return MathUtils.mix(this.parent.minV(), this.parent.maxV(), this.minV);
   }

   public float maxV() {
      return MathUtils.mix(this.parent.minV(), this.parent.maxV(), this.maxV);
   }
}
