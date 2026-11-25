package ivorius.ivtoolkit.models.animation;

import net.lax1dude.eaglercraft.v1_8.vector.Vector3f;

public class NodeFieldAnimatorVector3f extends NodeFieldAnimator<Vector3f> {
   public NodeFieldAnimatorVector3f() {
      super(new Vector3f());
   }

   public void interpolate(Vector3f dest, Vector3f left, Vector3f right, float progress) {
      float rev = 1.0F - progress;
      dest.set(left.x * rev + right.x * progress, left.y * rev + right.y * progress, left.z * rev + right.z * progress);
   }
}
