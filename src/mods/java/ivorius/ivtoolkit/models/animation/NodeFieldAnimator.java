package ivorius.ivtoolkit.models.animation;

import java.util.ArrayList;
import java.util.List;

public abstract class NodeFieldAnimator<F> {
   private List<NodeFieldAnimator<F>.Keyframe> keyframes = new ArrayList();
   private F temp;

   public NodeFieldAnimator(F temp) {
      this.temp = temp;
   }

   public void addKeyframe(F object, float keyframe) {
      this.keyframes.add(new NodeFieldAnimator.Keyframe(object, keyframe));
   }

   public int keyframeCount() {
      return this.keyframes.size();
   }

   public void update(F field, float time, float alpha) {
      NodeFieldAnimator<F>.InterpolatedKeyframe interpolatedKeyframe = this.interpolatedKeyframe(time);
      this.interpolate(this.temp, interpolatedKeyframe.left, interpolatedKeyframe.right, interpolatedKeyframe.time);
      this.interpolate(field, field, this.temp, alpha);
   }

   public abstract void interpolate(F var1, F var2, F var3, float var4);

   private NodeFieldAnimator<F>.InterpolatedKeyframe interpolatedKeyframe(float time) {
      int rightIndex = this.rightKeyframeIndex(time);
      NodeFieldAnimator<F>.Keyframe rightKeyframe = (NodeFieldAnimator.Keyframe)this.keyframes.get(rightIndex);
      if (rightIndex == 0) {
         return new NodeFieldAnimator.InterpolatedKeyframe(rightKeyframe.object);
      } else {
         NodeFieldAnimator<F>.Keyframe leftKeyframe = (NodeFieldAnimator.Keyframe)this.keyframes.get(rightIndex - 1);
         return new NodeFieldAnimator.InterpolatedKeyframe(leftKeyframe.object, rightKeyframe.object, (time - leftKeyframe.time) / (rightKeyframe.time - leftKeyframe.time));
      }
   }

   private int rightKeyframeIndex(float time) {
      for(int i = 0; i < this.keyframes.size(); ++i) {
         if (((NodeFieldAnimator.Keyframe)this.keyframes.get(i)).time > time) {
            return i;
         }
      }

      return this.keyframes.size() - 1;
   }

   private class InterpolatedKeyframe {
      public F left;
      public F right;
      public float time;

      public InterpolatedKeyframe(F left) {
         this.left = left;
         this.right = left;
         this.time = 0.0F;
      }

      public InterpolatedKeyframe(F left, F right, float time) {
         this.left = left;
         this.right = right;
         this.time = time;
      }
   }

   protected class Keyframe {
      public F object;
      public float time;

      public Keyframe(F object, float time) {
         this.object = object;
         this.time = time;
      }
   }
}
