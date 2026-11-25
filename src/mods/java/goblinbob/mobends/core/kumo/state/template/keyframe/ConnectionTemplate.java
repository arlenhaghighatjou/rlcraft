package goblinbob.mobends.core.kumo.state.template.keyframe;

import goblinbob.mobends.core.kumo.state.IKumoValidationContext;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.kumo.state.template.TriggerConditionTemplate;

public class ConnectionTemplate {
   public int targetNodeIndex;
   public TriggerConditionTemplate triggerCondition;
   public float transitionDuration = 0.0F;
   public ConnectionTemplate.Easing transitionEasing = ConnectionTemplate.Easing.EASE_IN_OUT;

   public ConnectionTemplate() {
   }

   public void validate(IKumoValidationContext context) throws MalformedKumoTemplateException {
      if (this.triggerCondition == null) {
         throw new MalformedKumoTemplateException("No trigger condition has been specified.");
      } else {
         this.triggerCondition.validate(context);
      }
   }

   public static enum Easing {
      LINEAR,
      EASE_IN,
      EASE_OUT,
      EASE_IN_OUT;

      private Easing() {
      }
   }
}
