package net.ilexiconn.llibrary.client.model.tabula.animation;

import java.util.List;
import java.util.function.Consumer;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.model.tabula.ITabulaModelAnimator;
import net.ilexiconn.llibrary.client.model.tabula.TabulaModel;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationComponentContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationContainer;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.ilexiconn.llibrary.server.animation.NamedAnimation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class AnimationPlayerAnimator<T extends Entity & IAnimatedEntity> implements ITabulaModelAnimator<T> {
   public AnimationPlayerAnimator() {
   }

   public void setRotationAngles(TabulaModel model, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
      float partialTicks = LLibrary.PROXY.getPartialTicks();
      Animation anim = ((IAnimatedEntity)entity).getAnimation();
      if (anim instanceof NamedAnimation) {
         TabulaAnimationContainer animation = model.getAnimation(((NamedAnimation)anim).getName());
         if (animation != null) {
            this.prepareAnimation(model, entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, partialTicks);
            animation.getComponents().entrySet().forEach((entry) -> {
               for(TabulaAnimationComponentContainer component : (List)entry.getValue()) {
                  this.applyComponent(component, model.getCubeByIdentifier((String)entry.getKey()), entity, partialTicks);
               }

            });
            this.finishAnimation(model, entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, partialTicks);
         }
      }
   }

   protected void prepareAnimation(TabulaModel model, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float partialTicks) {
   }

   protected void finishAnimation(TabulaModel model, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float partialTicks) {
   }

   private void applyComponent(TabulaAnimationComponentContainer component, AdvancedModelRenderer cube, T entity, float partialTicks) {
      float tick = (float)((IAnimatedEntity)entity).getAnimationTick() + partialTicks;
      double progress = (double)(tick - (float)component.getStartKey()) / (double)component.getLength();
      progress = MathHelper.clamp(progress, 0.0D, 1.0D);
      if (tick >= (float)component.getStartKey()) {
         cube.rotateAngleX = (float)((double)cube.rotateAngleX + Math.toRadians(component.getRotationOffset()[0]));
         cube.rotateAngleY = (float)((double)cube.rotateAngleY + Math.toRadians(component.getRotationOffset()[1]));
         cube.rotateAngleZ = (float)((double)cube.rotateAngleZ + Math.toRadians(component.getRotationOffset()[2]));
         cube.rotationPointX = (float)((double)cube.rotationPointX + component.getPositionOffset()[0]);
         cube.rotationPointY = (float)((double)cube.rotationPointY + component.getPositionOffset()[1]);
         cube.rotationPointZ = (float)((double)cube.rotationPointZ + component.getPositionOffset()[2]);
         cube.scaleX = (float)((double)cube.scaleX + component.getScaleOffset()[0]);
         cube.scaleY = (float)((double)cube.scaleY + component.getScaleOffset()[1]);
         cube.scaleZ = (float)((double)cube.scaleZ + component.getScaleOffset()[2]);
         cube.opacity = (float)((double)cube.opacity + component.getOpacityOffset());
      }

      cube.rotateAngleX = (float)((double)cube.rotateAngleX + Math.toRadians(component.getRotationChange()[0] * progress));
      cube.rotateAngleY = (float)((double)cube.rotateAngleY + Math.toRadians(component.getRotationChange()[1] * progress));
      cube.rotateAngleZ = (float)((double)cube.rotateAngleZ + Math.toRadians(component.getRotationChange()[2] * progress));
      cube.rotationPointX = (float)((double)cube.rotationPointX + component.getPositionChange()[0] * progress);
      cube.rotationPointY = (float)((double)cube.rotationPointY + component.getPositionChange()[1] * progress);
      cube.rotationPointZ = (float)((double)cube.rotationPointZ + component.getPositionChange()[2] * progress);
      cube.scaleX = (float)((double)cube.scaleX + component.getScaleChange()[0] * progress);
      cube.scaleY = (float)((double)cube.scaleY + component.getScaleChange()[1] * progress);
      cube.scaleZ = (float)((double)cube.scaleZ + component.getScaleChange()[2] * progress);
      cube.opacity = (float)((double)cube.opacity + component.getOpacityChange() * progress);
   }
}
