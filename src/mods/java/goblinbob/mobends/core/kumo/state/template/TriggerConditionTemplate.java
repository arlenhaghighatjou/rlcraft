package goblinbob.mobends.core.kumo.state.template;

import goblinbob.mobends.core.kumo.state.IKumoValidationContext;

public class TriggerConditionTemplate {
   public String type;

   public TriggerConditionTemplate() {
   }

   public String getType() {
      return this.type;
   }

   public void validate(IKumoValidationContext context) throws MalformedKumoTemplateException {
   }
}
