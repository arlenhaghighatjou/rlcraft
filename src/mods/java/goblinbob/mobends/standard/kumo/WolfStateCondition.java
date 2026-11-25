package goblinbob.mobends.standard.kumo;

import goblinbob.mobends.core.kumo.state.condition.ITriggerCondition;
import goblinbob.mobends.core.kumo.state.condition.ITriggerConditionContext;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.kumo.state.template.TriggerConditionTemplate;
import goblinbob.mobends.standard.data.WolfData;

public class WolfStateCondition implements ITriggerCondition {
   private final WolfStateCondition.State state;

   public WolfStateCondition(WolfStateCondition.Template template) throws MalformedKumoTemplateException {
      if (template.state == null) {
         throw new MalformedKumoTemplateException("No 'state' property given for trigger condition.");
      } else {
         this.state = template.state;
      }
   }

   public boolean isConditionMet(ITriggerConditionContext context) throws MalformedKumoTemplateException {
      WolfData wolfData;
      try {
         wolfData = (WolfData)context.getEntityData();
      } catch (ClassCastException var4) {
         throw new MalformedKumoTemplateException("A wolf_state trigger condition was used on something other than a wolf.");
      }

      switch(this.state) {
      case SITTING:
         return wolfData.isSitting();
      default:
         return false;
      }
   }

   public static enum State {
      SITTING;

      private State() {
      }
   }

   public static class Template extends TriggerConditionTemplate {
      public WolfStateCondition.State state;

      public Template() {
      }
   }
}
