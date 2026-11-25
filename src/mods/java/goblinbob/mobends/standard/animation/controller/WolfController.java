package goblinbob.mobends.standard.animation.controller;

import goblinbob.mobends.core.animation.controller.IAnimationController;
import goblinbob.mobends.core.animation.keyframe.AnimationLoader;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.kumo.state.IKumoInstancingContext;
import goblinbob.mobends.core.kumo.state.KumoAnimatorState;
import goblinbob.mobends.core.kumo.state.template.AnimatorTemplate;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.util.GsonResources;
import goblinbob.mobends.standard.data.WolfData;
import java.io.IOException;
import java.util.Collection;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class WolfController implements IAnimationController<WolfData> {
   protected static final ResourceLocation WOLF_ANIMATOR = new ResourceLocation("mobends", "animators/wolf.json");
   protected AnimatorTemplate animatorTemplate;
   protected KumoAnimatorState<WolfData> kumoAnimatorState;

   public WolfController() {
      try {
         this.animatorTemplate = (AnimatorTemplate)GsonResources.get(WOLF_ANIMATOR, AnimatorTemplate.class);
         this.kumoAnimatorState = new KumoAnimatorState<WolfData>(this.animatorTemplate, (key) -> {
            try {
               return AnimationLoader.loadFromPath(key);
            } catch (IOException var2) {
               var2.printStackTrace();
               return null;
            }
         });
      } catch (MalformedKumoTemplateException | IOException var2) {
         var2.printStackTrace();
      }

   }

   public Collection<String> perform(WolfData data) {
      EntityWolf wolf = (EntityWolf)data.getEntity();
      float ticks = (float)wolf.ticksExisted + DataUpdateHandler.partialTicks;

      try {
         this.kumoAnimatorState.update(data, DataUpdateHandler.ticksPerFrame);
      } catch (MalformedKumoTemplateException var5) {
         var5.printStackTrace();
      }

      if (wolf.isChild()) {
         data.head.offsetScale = 0.5F;
         data.head.globalOffset.set(0.0F, 5.0F, -2.0F);
      } else {
         data.head.offsetScale = 1.0F;
         data.head.globalOffset.set(0.0F, 0.0F, 0.0F);
      }

      data.head.position.set(0.0F, -0.5F, -13.0F);
      data.head.rotation.localRotateY(((Float)data.headYaw.get()).floatValue()).finish();
      data.head.rotation.localRotateX(((Float)data.headPitch.get()).floatValue()).finish();
      data.head.rotation.localRotateZ((wolf.getInterestedAngle(DataUpdateHandler.partialTicks) + wolf.getShakeAngle(DataUpdateHandler.partialTicks, 0.0F)) * 57.295776F).finish();
      data.mane.rotation.localRotateZ(wolf.getShakeAngle(DataUpdateHandler.partialTicks, -0.08F) * 57.295776F).finish();
      data.tail.rotation.localRotateZ(wolf.getShakeAngle(DataUpdateHandler.partialTicks, -0.2F) * 57.295776F).finish();
      data.tail.rotation.localRotateZ(wolf.getInterestedAngle(DataUpdateHandler.partialTicks) * MathHelper.sin(ticks) * 20.0F).finish();
      data.tail.rotation.localRotateX(wolf.getTailRotation() * 57.295776F - 90.0F).finish();
      data.head.offset.set(0.0F, 0.0F, 0.0F);
      return null;
   }
}
