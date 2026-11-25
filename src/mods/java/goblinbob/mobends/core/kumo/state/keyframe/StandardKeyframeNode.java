package goblinbob.mobends.core.kumo.state.keyframe;

import goblinbob.mobends.core.animation.keyframe.Bone;
import goblinbob.mobends.core.animation.keyframe.KeyframeAnimation;
import goblinbob.mobends.core.kumo.state.ConnectionState;
import goblinbob.mobends.core.kumo.state.IKumoContext;
import goblinbob.mobends.core.kumo.state.IKumoInstancingContext;
import goblinbob.mobends.core.kumo.state.INodeState;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.kumo.state.template.keyframe.ConnectionTemplate;
import goblinbob.mobends.core.kumo.state.template.keyframe.KeyframeNodeTemplate;
import goblinbob.mobends.core.kumo.state.template.keyframe.StandardKeyframeNodeTemplate;
import java.util.ArrayList;
import java.util.List;

public class StandardKeyframeNode implements INodeState {
   public final KeyframeAnimation animation;
   private int animationDuration;
   private final int startFrame;
   private final float playbackSpeed;
   private final boolean looping;
   List<ConnectionState> connections;
   private float progress;

   public StandardKeyframeNode(IKumoInstancingContext context, StandardKeyframeNodeTemplate nodeTemplate) {
      this(nodeTemplate.animationKey != null ? context.getAnimation(nodeTemplate.animationKey) : null, nodeTemplate.startFrame, nodeTemplate.playbackSpeed, nodeTemplate.looping);
   }

   public StandardKeyframeNode(KeyframeAnimation animation, int startFrame, float playbackSpeed, boolean looping) {
      this.connections = new ArrayList();
      this.animation = animation;
      this.startFrame = startFrame;
      this.playbackSpeed = playbackSpeed;
      this.looping = looping;
      if (animation != null) {
         this.animationDuration = 0;

         for(Bone bone : animation.bones.values()) {
            if (bone.keyframes.size() > this.animationDuration) {
               this.animationDuration = bone.keyframes.size();
            }
         }
      }

      this.progress = (float)this.startFrame;
   }

   public void parseConnections(List<INodeState> nodeStates, KeyframeNodeTemplate template) throws MalformedKumoTemplateException {
      if (template.connections != null) {
         for(ConnectionTemplate connectionTemplate : template.connections) {
            this.connections.add(ConnectionState.createFromTemplate(nodeStates, connectionTemplate));
         }
      }

   }

   public void start(IKumoContext context) {
      this.progress = (float)this.startFrame;

      for(ConnectionState connection : this.connections) {
         connection.triggerCondition.onNodeStarted(context);
      }

   }

   public void update(IKumoContext context, float deltaTime) {
      if (this.animation != null) {
         if (this.looping) {
            for(this.progress += this.playbackSpeed * deltaTime; this.progress >= (float)(this.animationDuration - 1); this.progress -= (float)(this.animationDuration - 1)) {
               ;
            }
         } else if (this.progress < (float)(this.animationDuration - 2)) {
            this.progress = Math.min(this.progress + this.playbackSpeed * deltaTime, (float)(this.animationDuration - 2));
         }
      }

   }

   public KeyframeAnimation getAnimation() {
      return this.animation;
   }

   public boolean isAnimationFinished() {
      return this.animation == null || !this.looping && this.progress >= (float)(this.animationDuration - 2);
   }

   public float getProgress() {
      return this.progress;
   }

   public Iterable<ConnectionState> getConnections() {
      return this.connections;
   }
}
