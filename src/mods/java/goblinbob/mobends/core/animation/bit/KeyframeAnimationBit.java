package goblinbob.mobends.core.animation.bit;

import goblinbob.mobends.core.animation.keyframe.AnimationLoader;
import goblinbob.mobends.core.animation.keyframe.ArmatureMask;
import goblinbob.mobends.core.animation.keyframe.Bone;
import goblinbob.mobends.core.animation.keyframe.Keyframe;
import goblinbob.mobends.core.animation.keyframe.KeyframeAnimation;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.data.EntityData;
import java.io.IOException;
import java.util.Map.Entry;
import net.minecraft.util.ResourceLocation;

public class KeyframeAnimationBit<T extends EntityData<?>> extends AnimationBit<T> {
   protected KeyframeAnimation performedAnimation = null;
   private ArmatureMask mask = null;
   private KeyframeAnimationBit.TriggerMode triggerMode = KeyframeAnimationBit.TriggerMode.RETRIGGER;
   private float keyframeIndex = 0.0F;
   private float animationSpeed;

   public KeyframeAnimationBit(ResourceLocation animationResource, float animationSpeed) {
      this.animationSpeed = animationSpeed;

      try {
         this.performedAnimation = AnimationLoader.loadFromResource(animationResource);
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }

   public void setAnimation(KeyframeAnimation animation) {
      this.performedAnimation = animation;
   }

   public void clearAnimation() {
      this.performedAnimation = null;
   }

   public void setMask(ArmatureMask mask) {
      this.mask = mask;
   }

   public void setAnimationSpeed(float animationSpeed) {
      this.animationSpeed = animationSpeed;
   }

   public KeyframeAnimationBit<T> setTriggerMode(KeyframeAnimationBit.TriggerMode triggerMode) {
      this.triggerMode = triggerMode;
      return this;
   }

   public void onPlay(T entityData) {
      if (this.triggerMode == KeyframeAnimationBit.TriggerMode.RETRIGGER) {
         this.keyframeIndex = 0.0F;
      }

   }

   public void perform(T entityData) {
      if (this.performedAnimation != null) {
         int minKeyframes = Integer.MAX_VALUE;
         int index = (int)this.keyframeIndex;
         int nextIndex = index + 1;
         if (nextIndex >= minKeyframes - 1) {
            nextIndex = minKeyframes - 1;
         }

         float progress = this.keyframeIndex - (float)index;
         if (this.performedAnimation.bones.containsKey("root") && this.shouldPartBeAffected("root")) {
            Bone rootBone = (Bone)this.performedAnimation.bones.get("root");
            Keyframe keyframe = (Keyframe)rootBone.keyframes.get(index);
            Keyframe nextFrame = (Keyframe)rootBone.keyframes.get(nextIndex);
            float x = keyframe.position[0] + (nextFrame.position[0] - keyframe.position[0]) * progress;
            float y = keyframe.position[1] + (nextFrame.position[1] - keyframe.position[1]) * progress;
            float z = keyframe.position[2] + (nextFrame.position[2] - keyframe.position[2]) * progress;
            float x0 = keyframe.rotation[0];
            float y0 = keyframe.rotation[1];
            float z0 = keyframe.rotation[2];
            float w0 = keyframe.rotation[3];
            float x1 = nextFrame.rotation[0];
            float y1 = nextFrame.rotation[1];
            float z1 = nextFrame.rotation[2];
            float w1 = nextFrame.rotation[3];
            entityData.renderRotation.set(x0 + (x1 - x0) * progress, y0 + (y1 - y0) * progress, z0 + (z1 - z0) * progress, w0 + (w1 - w0) * progress);
            entityData.globalOffset.set(x, y, z);
         }

         for(Entry<String, Bone> entry : this.performedAnimation.bones.entrySet()) {
            Bone bone = (Bone)entry.getValue();
            minKeyframes = bone.keyframes.size();
            Object part = entityData.getPartForName((String)entry.getKey());
            if (part != null && this.shouldPartBeAffected((String)entry.getKey())) {
               Keyframe keyframe = (Keyframe)bone.keyframes.get(index);
               Keyframe nextFrame = (Keyframe)bone.keyframes.get(nextIndex);
               if (keyframe != null && nextFrame != null && part instanceof IModelPart) {
                  IModelPart box = (IModelPart)part;
                  float x0 = keyframe.rotation[0];
                  float y0 = keyframe.rotation[1];
                  float z0 = keyframe.rotation[2];
                  float w0 = keyframe.rotation[3];
                  float x1 = nextFrame.rotation[0];
                  float y1 = nextFrame.rotation[1];
                  float z1 = nextFrame.rotation[2];
                  float w1 = nextFrame.rotation[3];
                  box.getRotation().set(x0 + (x1 - x0) * progress, y0 + (y1 - y0) * progress, z0 + (z1 - z0) * progress, w0 + (w1 - w0) * progress);
               }
            }
         }

         this.keyframeIndex += DataUpdateHandler.ticksPerFrame * this.animationSpeed;
         if (this.keyframeIndex >= (float)(minKeyframes - 1)) {
            this.keyframeIndex -= (float)(minKeyframes - 1);
         }
      }

   }

   private boolean shouldPartBeAffected(String partName) {
      return this.mask == null || this.mask.doesAllow(partName);
   }

   public String[] getActions(T entityData) {
      return null;
   }

   public static enum TriggerMode {
      RETRIGGER,
      CONTINUE;

      private TriggerMode() {
      }
   }
}
