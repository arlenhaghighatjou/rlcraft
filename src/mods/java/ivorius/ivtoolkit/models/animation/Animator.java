package ivorius.ivtoolkit.models.animation;

import ivorius.ivtoolkit.models.Animation;
import ivorius.ivtoolkit.models.NodeAnimation;
import java.util.ArrayList;
import java.util.List;

public class Animator {
   private Animation animation;
   private List<NodeAnimator> nodeAnimators = new ArrayList();
   private boolean loop;

   public Animator(Animation animation, boolean loop) {
      this.animation = animation;

      for(NodeAnimation nodeAnimation : animation.nodeAnimations) {
         this.nodeAnimators.add(new NodeAnimator(nodeAnimation));
      }

      this.loop = loop;
   }

   public void update(float time, float alpha) {
      if (this.loop) {
         time %= this.animation.duration;
      }

      for(NodeAnimator nodeAnimator : this.nodeAnimators) {
         nodeAnimator.update(time, alpha);
      }

   }

   public void endAnimation() {
      this.update(0.0F, 0.0F);
   }
}
