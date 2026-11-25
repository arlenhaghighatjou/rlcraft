package goblinbob.mobends.core.animation.keyframe;

public class Keyframe {
   public float[] position;
   public float[] rotation;
   public float[] scale;

   public Keyframe() {
   }

   public void mirrorRotationYZ() {
      this.rotation[1] *= -1.0F;
      this.rotation[2] *= -1.0F;
   }

   public void swapRotationYZ() {
      float y = this.rotation[1];
      this.rotation[1] = this.rotation[2];
      this.rotation[2] = y;
   }
}
