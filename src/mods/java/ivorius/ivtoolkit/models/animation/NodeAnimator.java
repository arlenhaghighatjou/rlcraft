package ivorius.ivtoolkit.models.animation;

import ivorius.ivtoolkit.models.Node;
import ivorius.ivtoolkit.models.NodeAnimation;
import ivorius.ivtoolkit.models.NodeKeyframe;
import org.lwjgl.util.vector.Quaternion;
import net.lax1dude.eaglercraft.v1_8.vector.Vector3f;

public class NodeAnimator {
   private NodeAnimation nodeAnimation;
   private NodeFieldAnimator<Vector3f> animatorTranslation;
   private NodeFieldAnimator<Vector3f> animatorScale;
   private NodeFieldAnimator<Quaternion> animatorRotation;

   public NodeAnimator(NodeAnimation nodeAnimation) {
      this.nodeAnimation = nodeAnimation;
      this.animatorTranslation = new NodeFieldAnimatorVector3f();
      this.animatorScale = new NodeFieldAnimatorVector3f();
      this.animatorRotation = new NodeFieldAnimatorQuaternion();

      for(NodeKeyframe nodeKeyframe : nodeAnimation.keyframes) {
         if (nodeKeyframe.translation != null) {
            this.animatorTranslation.addKeyframe(nodeKeyframe.translation, nodeKeyframe.keytime);
         }

         if (nodeKeyframe.scale != null) {
            this.animatorScale.addKeyframe(nodeKeyframe.scale, nodeKeyframe.keytime);
         }

         if (nodeKeyframe.rotation != null) {
            this.animatorRotation.addKeyframe(nodeKeyframe.rotation, nodeKeyframe.keytime);
         }
      }

      if (this.animatorTranslation.keyframeCount() == 0) {
         this.animatorTranslation.addKeyframe(nodeAnimation.node.translation, 0.0F);
      }

      if (this.animatorScale.keyframeCount() == 0) {
         this.animatorScale.addKeyframe(nodeAnimation.node.scale, 0.0F);
      }

      if (this.animatorRotation.keyframeCount() == 0) {
         this.animatorRotation.addKeyframe(nodeAnimation.node.rotation, 0.0F);
      }

   }

   public void update(float time, float alpha) {
      Node node = this.nodeAnimation.node;
      this.animatorTranslation.update(node.translation, time, alpha);
      this.animatorScale.update(node.scale, time, alpha);
      this.animatorRotation.update(node.rotation, time, alpha);
   }
}
