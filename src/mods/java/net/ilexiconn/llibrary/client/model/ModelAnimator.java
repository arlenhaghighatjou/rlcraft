package net.ilexiconn.llibrary.client.model;

import java.util.HashMap;
import java.util.function.Function;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelAnimator {
   private int tempTick = 0;
   private int prevTempTick;
   private boolean correctAnimation = false;
   private IAnimatedEntity entity;
   private HashMap<ModelRenderer, Transform> transformMap = new HashMap();
   private HashMap<ModelRenderer, Transform> prevTransformMap = new HashMap();

   private ModelAnimator() {
   }

   public static ModelAnimator create() {
      return new ModelAnimator();
   }

   public IAnimatedEntity getEntity() {
      return this.entity;
   }

   public void update(IAnimatedEntity entity) {
      this.tempTick = this.prevTempTick = 0;
      this.correctAnimation = false;
      this.entity = entity;
      this.transformMap.clear();
      this.prevTransformMap.clear();
   }

   public boolean setAnimation(Animation animation) {
      this.tempTick = this.prevTempTick = 0;
      this.correctAnimation = this.entity.getAnimation() == animation;
      return this.correctAnimation;
   }

   public void startKeyframe(int duration) {
      if (this.correctAnimation) {
         this.prevTempTick = this.tempTick;
         this.tempTick += duration;
      }
   }

   public void setStaticKeyframe(int duration) {
      this.startKeyframe(duration);
      this.endKeyframe(true);
   }

   public void resetKeyframe(int duration) {
      this.startKeyframe(duration);
      this.endKeyframe();
   }

   public void rotate(ModelRenderer box, float x, float y, float z) {
      if (this.correctAnimation) {
         this.getTransform(box).addRotation(x, y, z);
      }
   }

   public void move(ModelRenderer box, float x, float y, float z) {
      if (this.correctAnimation) {
         this.getTransform(box).addOffset(x, y, z);
      }
   }

   private Transform getTransform(ModelRenderer box) {
      return (Transform)this.transformMap.computeIfAbsent(box, (b) -> {
         return new Transform();
      });
   }

   public void endKeyframe() {
      this.endKeyframe(false);
   }

   private void endKeyframe(boolean stationary) {
      if (this.correctAnimation) {
         int animationTick = this.entity.getAnimationTick();
         if (animationTick >= this.prevTempTick && animationTick < this.tempTick) {
            if (stationary) {
               for(ModelRenderer box : this.prevTransformMap.keySet()) {
                  Transform transform = (Transform)this.prevTransformMap.get(box);
                  box.rotateAngleX += transform.getRotationX();
                  box.rotateAngleY += transform.getRotationY();
                  box.rotateAngleZ += transform.getRotationZ();
                  box.rotationPointX += transform.getOffsetX();
                  box.rotationPointY += transform.getOffsetY();
                  box.rotationPointZ += transform.getOffsetZ();
               }
            } else {
               float tick = ((float)(animationTick - this.prevTempTick) + LLibrary.PROXY.getPartialTicks()) / (float)(this.tempTick - this.prevTempTick);
               float inc = MathHelper.sin((float)((double)tick * 3.141592653589793D / 2.0D));
               float dec = 1.0F - inc;

               for(ModelRenderer box : this.prevTransformMap.keySet()) {
                  Transform transform = (Transform)this.prevTransformMap.get(box);
                  box.rotateAngleX += dec * transform.getRotationX();
                  box.rotateAngleY += dec * transform.getRotationY();
                  box.rotateAngleZ += dec * transform.getRotationZ();
                  box.rotationPointX += dec * transform.getOffsetX();
                  box.rotationPointY += dec * transform.getOffsetY();
                  box.rotationPointZ += dec * transform.getOffsetZ();
               }

               for(ModelRenderer box : this.transformMap.keySet()) {
                  Transform transform = (Transform)this.transformMap.get(box);
                  box.rotateAngleX += inc * transform.getRotationX();
                  box.rotateAngleY += inc * transform.getRotationY();
                  box.rotateAngleZ += inc * transform.getRotationZ();
                  box.rotationPointX += inc * transform.getOffsetX();
                  box.rotationPointY += inc * transform.getOffsetY();
                  box.rotationPointZ += inc * transform.getOffsetZ();
               }
            }
         }

         if (!stationary) {
            this.prevTransformMap.clear();
            this.prevTransformMap.putAll(this.transformMap);
            this.transformMap.clear();
         }

      }
   }
}
