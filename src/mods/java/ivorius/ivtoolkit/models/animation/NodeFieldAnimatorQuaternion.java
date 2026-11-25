package ivorius.ivtoolkit.models.animation;

import org.lwjgl.util.vector.Quaternion;

public class NodeFieldAnimatorQuaternion extends NodeFieldAnimator<Quaternion> {
   public NodeFieldAnimatorQuaternion() {
      super(new Quaternion());
   }

   public void interpolate(Quaternion dest, Quaternion left, Quaternion right, float progress) {
      float rev = 1.0F - progress;
      dest.set(left.x * rev + right.x * progress, left.y * rev + right.y * progress, left.z * rev + right.z * progress, left.w * rev + right.w * progress);
   }
}
