package goblinbob.mobends.core.kumo.state.keyframe;

import goblinbob.mobends.core.animation.keyframe.ArmatureMask;
import goblinbob.mobends.core.animation.keyframe.Bone;
import goblinbob.mobends.core.animation.keyframe.Keyframe;
import goblinbob.mobends.core.animation.keyframe.KeyframeAnimation;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.kumo.state.ConnectionState;
import goblinbob.mobends.core.kumo.state.IKumoContext;
import goblinbob.mobends.core.kumo.state.IKumoInstancingContext;
import goblinbob.mobends.core.kumo.state.ILayerState;
import goblinbob.mobends.core.kumo.state.INodeState;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.kumo.state.template.keyframe.ConnectionTemplate;
import goblinbob.mobends.core.kumo.state.template.keyframe.KeyframeLayerTemplate;
import goblinbob.mobends.core.kumo.state.template.keyframe.KeyframeNodeTemplate;
import goblinbob.mobends.core.util.KeyframeUtils;
import goblinbob.mobends.core.util.Tween;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class KeyframeLayerState implements ILayerState {
   private List<INodeState> nodeStates = new ArrayList();
   private ArmatureMask mask;
   private INodeState previousNode;
   private INodeState currentNode;
   private float transitionProgress = 0.0F;
   private float transitionDuration = 0.0F;
   private ConnectionTemplate.Easing transitionEasing = ConnectionTemplate.Easing.EASE_IN_OUT;

   public KeyframeLayerState(IKumoInstancingContext context, KeyframeLayerTemplate layerTemplate) throws MalformedKumoTemplateException {
      this.mask = layerTemplate.mask;

      for(KeyframeNodeTemplate nodeTemplate : layerTemplate.nodes) {
         this.nodeStates.add(KeyframeNodeRegistry.INSTANCE.createFromTemplate(context, nodeTemplate));
      }

      for(int i = 0; i < this.nodeStates.size(); ++i) {
         ((INodeState)this.nodeStates.get(i)).parseConnections(this.nodeStates, (KeyframeNodeTemplate)layerTemplate.nodes.get(i));
      }

      try {
         this.currentNode = (INodeState)this.nodeStates.get(layerTemplate.entryNode);
      } catch (IndexOutOfBoundsException var5) {
         throw new MalformedKumoTemplateException("Entry node index is out of bounds.");
      }
   }

   public void start(IKumoContext context) {
      this.currentNode.start(context);
   }

   public void update(IKumoContext context, float deltaTime) throws MalformedKumoTemplateException {
      EntityData<?> data = context.getEntityData();
      if (this.currentNode != null) {
         KeyframeAnimation animation = this.currentNode.getAnimation();
         if (animation != null) {
            this.applyRestPose(data, animation);
            if (this.previousNode != null) {
               float t = this.transitionProgress / this.transitionDuration;
               switch(this.transitionEasing) {
               case EASE_IN:
                  t = (float)Tween.easeIn((double)t, 2.0D);
                  break;
               case EASE_OUT:
                  t = (float)Tween.easeOut((double)t, 2.0D);
                  break;
               case EASE_IN_OUT:
                  t = (float)Tween.easeInOut((double)t, 2.0D);
               case LINEAR:
               }

               KeyframeAnimation previousAnimation = this.previousNode.getAnimation();
               this.applyKeyframeAnimation(data, previousAnimation, this.previousNode.getProgress(), 1.0F - t);
               this.applyKeyframeAnimation(data, animation, this.currentNode.getProgress(), t);
               this.transitionProgress += deltaTime;
               if (this.transitionProgress >= this.transitionDuration) {
                  this.previousNode = null;
               }
            } else {
               this.applyKeyframeAnimation(data, animation, this.currentNode.getProgress(), 1.0F);
            }
         }
      }

      context.setCurrentNode(this.currentNode);

      for(INodeState node : this.nodeStates) {
         node.update(context, deltaTime);
      }

      for(ConnectionState connection : this.currentNode.getConnections()) {
         if (connection.triggerCondition.isConditionMet(context)) {
            this.transitionDuration = connection.transitionDuration;
            this.transitionEasing = connection.transitionEasing;
            if (this.transitionDuration == 0.0F) {
               this.previousNode = null;
            } else {
               this.previousNode = this.currentNode;
               this.transitionProgress = 0.0F;
            }

            this.currentNode = connection.targetNode;
            this.currentNode.start(context);
            break;
         }
      }

   }

   public void applyRestPose(EntityData<?> entityData, KeyframeAnimation animation) {
      if (this.shouldPartBeAffected("root") && animation.bones.containsKey("root")) {
         entityData.globalOffset.set(0.0F, 0.0F, 0.0F);
      }

      if (this.shouldPartBeAffected("root") && animation.bones.containsKey("root") || this.shouldPartBeAffected("centerRotation") && animation.bones.containsKey("centerRotation")) {
         entityData.centerRotation.set(0.0F, 0.0F, 0.0F, 0.0F);
      }

      for(Entry<String, Bone> entry : animation.bones.entrySet()) {
         String key = (String)entry.getKey();
         if (this.shouldPartBeAffected(key)) {
            Object part = entityData.getPartForName(key);
            if (part instanceof IModelPart) {
               ((IModelPart)part).getRotation().set(0.0F, 0.0F, 0.0F, 0.0F);
               ((IModelPart)part).getOffset().set(0.0F, 0.0F, 0.0F);
            }
         }
      }

   }

   public void applyKeyframeAnimation(EntityData<?> entityData, KeyframeAnimation animation, float keyframeIndex, float amount) {
      int frameA = (int)keyframeIndex;
      int frameB = (int)keyframeIndex + 1;
      float tween = keyframeIndex - (float)frameA;
      if (this.shouldPartBeAffected("root") && animation.bones.containsKey("root")) {
         Bone rootBone = (Bone)animation.bones.get("root");
         Keyframe keyframe = (Keyframe)rootBone.keyframes.get(frameA);
         Keyframe nextFrame = (Keyframe)rootBone.keyframes.get(frameB);
         if (keyframe != null && nextFrame != null) {
            KeyframeUtils.tweenVectorAdditive(entityData.globalOffset, keyframe.position, nextFrame.position, tween, amount);
         }
      }

      if (this.shouldPartBeAffected("centerRotation") && animation.bones.containsKey("centerRotation")) {
         Bone rootBone = (Bone)animation.bones.get("centerRotation");
         Keyframe keyframe = (Keyframe)rootBone.keyframes.get(frameA);
         Keyframe nextFrame = (Keyframe)rootBone.keyframes.get(frameB);
         if (keyframe != null && nextFrame != null) {
            KeyframeUtils.tweenOrientationAdditive(entityData.centerRotation, keyframe.rotation, nextFrame.rotation, tween, amount);
            KeyframeUtils.tweenVectorAdditive(entityData.globalOffset, keyframe.position, nextFrame.position, tween, amount);
         }
      }

      for(Entry<String, Bone> entry : animation.bones.entrySet()) {
         String key = (String)entry.getKey();
         if (this.shouldPartBeAffected(key)) {
            Bone bone = (Bone)entry.getValue();
            Object part = entityData.getPartForName(key);
            if (part != null) {
               Keyframe keyframe = (Keyframe)bone.keyframes.get(frameA);
               Keyframe nextFrame = (Keyframe)bone.keyframes.get(frameB);
               if (keyframe != null && nextFrame != null && part instanceof IModelPart) {
                  KeyframeUtils.tweenOrientationAdditive(((IModelPart)part).getRotation(), keyframe.rotation, nextFrame.rotation, tween, amount);
                  KeyframeUtils.tweenVectorAdditive(((IModelPart)part).getOffset(), keyframe.position, nextFrame.position, tween, -amount);
               }
            }
         }
      }

   }

   private boolean shouldPartBeAffected(String partName) {
      return this.mask == null || this.mask.doesAllow(partName);
   }

   public static KeyframeLayerState createFromTemplate(IKumoInstancingContext data, KeyframeLayerTemplate layerTemplate) throws MalformedKumoTemplateException {
      return new KeyframeLayerState(data, layerTemplate);
   }
}
