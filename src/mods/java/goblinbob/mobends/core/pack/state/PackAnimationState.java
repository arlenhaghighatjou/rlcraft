package goblinbob.mobends.core.pack.state;

import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.kumo.state.KumoAnimatorState;
import goblinbob.mobends.core.kumo.state.template.AnimatorTemplate;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.pack.BendsPackData;

public class PackAnimationState {
   private KumoAnimatorState<EntityData<?>> kumoAnimatorState;
   private BendsPackData bendsPackData;

   public PackAnimationState() {
   }

   private void initFor(BendsPackData data, String animatedEntityKey) throws MalformedKumoTemplateException {
      this.bendsPackData = data;
      if (data.targets == null) {
         throw new MalformedKumoTemplateException("No targets were specified!");
      } else {
         AnimatorTemplate targetTemplate = (AnimatorTemplate)data.targets.get(animatedEntityKey);
         if (targetTemplate == null) {
            this.kumoAnimatorState = null;
         } else {
            this.kumoAnimatorState = new KumoAnimatorState<EntityData<?>>(targetTemplate, data);
         }
      }
   }

   public void update(EntityData<?> entityData, BendsPackData data, String animatedEntityKey, float deltaTime) throws MalformedKumoTemplateException {
      if (this.bendsPackData != data) {
         this.bendsPackData = data;

         try {
            this.initFor(data, animatedEntityKey);
         } catch (MalformedKumoTemplateException var6) {
            this.bendsPackData = null;
            throw var6;
         }
      }

      if (this.kumoAnimatorState != null) {
         this.kumoAnimatorState.update(entityData, deltaTime);
      }

   }
}
