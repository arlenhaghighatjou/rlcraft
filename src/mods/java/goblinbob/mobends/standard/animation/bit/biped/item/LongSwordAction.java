package goblinbob.mobends.standard.animation.bit.biped.item;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.animation.keyframe.AnimationLoader;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.kumo.state.IKumoInstancingContext;
import goblinbob.mobends.core.kumo.state.KumoAnimatorState;
import goblinbob.mobends.core.kumo.state.template.AnimatorTemplate;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.util.GsonResources;
import goblinbob.mobends.standard.data.BipedEntityData;
import java.io.IOException;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

public class LongSwordAction extends AnimationBit<BipedEntityData<?>> {
   protected static final ResourceLocation LONG_SWORD_ANIMATOR = new ResourceLocation("mobends", "animators/longsword.json");
   private final EnumHandSide handSide;
   protected AnimatorTemplate animatorTemplate;
   protected KumoAnimatorState<BipedEntityData<?>> kumoAnimatorState;

   public LongSwordAction(EnumHandSide handSide) {
      this.handSide = handSide;

      try {
         this.animatorTemplate = (AnimatorTemplate)GsonResources.get(LONG_SWORD_ANIMATOR, AnimatorTemplate.class);
         this.kumoAnimatorState = new KumoAnimatorState<BipedEntityData<?>>(this.animatorTemplate, (key) -> {
            try {
               return AnimationLoader.loadFromPath(key);
            } catch (IOException var2) {
               var2.printStackTrace();
               return null;
            }
         });
      } catch (MalformedKumoTemplateException | IOException var3) {
         var3.printStackTrace();
      }

   }

   public void perform(BipedEntityData<?> entityData) {
      try {
         this.kumoAnimatorState.update(entityData, DataUpdateHandler.ticksPerFrame);
      } catch (MalformedKumoTemplateException var3) {
         var3.printStackTrace();
      }

   }
}
